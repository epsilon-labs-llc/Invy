package jp.co.epsilonlabs.invy.commands;

import jp.co.epsilonlabs.invy.InvyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

public class InvyCommand implements CommandExecutor, TabCompleter {

    private final InvyPlugin plugin;

    public InvyCommand(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return List.of("gui", "reload", "grant");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("grant")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("grant")) {
            return List.of("30s", "3m", "1h", "12h", "1d");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Invy v" + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.GRAY + "/invy gui - アイテムGUIを開く");
            sender.sendMessage(ChatColor.GRAY + "/invy reload - 設定を再読み込み");
            sender.sendMessage(ChatColor.GRAY + "/invy grant <player> <time> - GUI権限を一時的に付与");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("invy.reload")) {
                    sender.sendMessage(ChatColor.RED + "この操作を行う権限がありません。");
                    return true;
                }
                plugin.reloadConfig();
                plugin.getItemManager().loadItems();
                sender.sendMessage(ChatColor.GREEN + "設定をリロードしました。");
                return true;
            }

            case "gui" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが使用できます。");
                    return true;
                }
                if (!player.hasPermission("invy.use")) {
                    player.sendMessage(ChatColor.RED + "この操作を行う権限がありません。");
                    return true;
                }
                plugin.getInvyGUI().open(player);
                return true;
            }

            case "grant" -> {
                if (!sender.hasPermission("invy.grant")) {
                    sender.sendMessage(ChatColor.RED + "この操作を行う権限がありません。");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "使用方法: /invy grant <player> <time>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "指定されたプレイヤーは見つかりません。");
                    return true;
                }

                try {
                    int seconds = parseTimeStringToSeconds(args[2]);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " にGUI権限を " + seconds + " 秒間付与しました");
                    // TODO: 実際の一時付与処理を追加
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "時間形式が不正です。例: 30s, 3m, 1h, 1d");
                }
                return true;
            }

            default -> {
                sender.sendMessage(ChatColor.RED + "無効なサブコマンドです。/invy で確認してください。");
                return true;
            }
        }
    }

    private int parseTimeStringToSeconds(String input) {
        int totalSeconds = 0;
        StringBuilder number = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                if (number.isEmpty()) throw new IllegalArgumentException();
                int value = Integer.parseInt(number.toString());
                switch (c) {
                    case 's' -> totalSeconds += value;
                    case 'm' -> totalSeconds += value * 60;
                    case 'h' -> totalSeconds += value * 3600;
                    case 'd' -> totalSeconds += value * 86400;
                    default -> throw new IllegalArgumentException();
                }
                number.setLength(0);
            }
        }

        if (!number.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return totalSeconds;
    }
}