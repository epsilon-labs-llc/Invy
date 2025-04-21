package jp.co.epsilonlabs.invy.commands;

import jp.co.epsilonlabs.invy.InvyPlugin;
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
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "使い方: /invy grant <player> <time>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
            return true;
        }

        long durationTicks = parseTime(args[2]);
        if (durationTicks <= 0) {
            sender.sendMessage(ChatColor.RED + "無効な時間です（例: 10s, 5m, 1h）");
            return true;
        }

        // 権限付与
        PermissionAttachment attachment = target.addAttachment(plugin);
        attachment.setPermission("invy.use", true);
        granted.put(target.getUniqueId(), attachment);

        sender.sendMessage(ChatColor.GREEN + target.getName() + " に一時的に invy.use を付与しました（" + args[2] + "）");
        target.sendMessage(ChatColor.YELLOW + "InvyのGUIを一時的に使用できます！（" + args[2] + "）");

        // ログ出力
        logger.info("[Invy] " + sender.getName() + " granted 'invy.use' permission to " + target.getName() + " for " + args[2]);

        // 時間が来たら解除
        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isOnline() && granted.containsKey(target.getUniqueId())) {
                    target.removeAttachment(granted.remove(target.getUniqueId()));
                    target.sendMessage(ChatColor.RED + "InvyのGUIの使用権限が期限切れになりました。");
                }
            }
        }.runTaskLater(plugin, durationTicks);

        return true;
    }

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