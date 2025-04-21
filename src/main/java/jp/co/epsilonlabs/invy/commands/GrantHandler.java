package jp.co.epsilonlabs.invy.commands;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class GrantHandler {

    private final InvyPlugin plugin;
    private final Map<UUID, PermissionAttachment> granted = new HashMap<>();
    private final Logger logger;

    public GrantHandler(InvyPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public boolean handleGrant(CommandSender sender, String[] args) {
        MessageManager messages = plugin.getMessageManager();

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + messages.get("player.not_found"));
            return true;
        }

        long durationTicks = parseTime(args[2]);
        if (durationTicks <= 0) {
            sender.sendMessage(ChatColor.RED + messages.get("grant.invalid_time"));
            return true;
        }

        // 一時的に invy.use 権限を付与
        PermissionAttachment attachment = target.addAttachment(plugin);
        attachment.setPermission("invy.use", true);
        granted.put(target.getUniqueId(), attachment);

        // メッセージ表示
        Map<String, String> vars = Map.of(
                "player", target.getName(),
                "time", args[2]
        );
        sender.sendMessage(ChatColor.GREEN + messages.getFormatted("grant.success", vars));
        target.sendMessage(ChatColor.YELLOW + messages.getFormatted("grant.notice", vars));

        // ログ出力
        logger.info("[Invy] " + sender.getName() + " granted 'invy.use' permission to " + target.getName() + " for " + args[2]);

        // 指定時間後に権限を自動で削除
        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isOnline() && granted.containsKey(target.getUniqueId())) {
                    target.removeAttachment(granted.remove(target.getUniqueId()));
                    target.sendMessage(ChatColor.RED + messages.get("grant.expired"));
                }
            }
        }.runTaskLater(plugin, durationTicks);

        return true;
    }

    // 時間をTickに変換する
    private long parseTime(String timeStr) {
        try {
            long value = Long.parseLong(timeStr.replaceAll("[smhd]", ""));
            if (timeStr.endsWith("s")) return value * 20;
            if (timeStr.endsWith("m")) return value * 20 * 60;
            if (timeStr.endsWith("h")) return value * 20 * 60 * 60;
            if (timeStr.endsWith("d")) return value * 20 * 60 * 60 * 24;
        } catch (NumberFormatException ignored) {}
        return -1;
    }
}