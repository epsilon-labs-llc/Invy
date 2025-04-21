package jp.co.epsilonlabs.invy.commands;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.item.ItemManager;
import jp.co.epsilonlabs.invy.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InvyCommand implements CommandExecutor, TabCompleter {

    private final InvyPlugin plugin;
    private final GrantHandler grantHandler;

    public InvyCommand(InvyPlugin plugin) {
        this.plugin = plugin;
        this.grantHandler = new GrantHandler(plugin);
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
        MessageManager messages = plugin.getMessageManager();

        // 引数なし → 利用可能なコマンドを表示（権限によって制限）
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Invy v" + plugin.getDescription().getVersion());

            if (sender.hasPermission("invy.use")) {
                sender.sendMessage(ChatColor.GRAY + messages.get("help.gui"));
            }
            if (sender.hasPermission("invy.reload")) {
                sender.sendMessage(ChatColor.GRAY + messages.get("help.reload"));
            }
            if (sender.hasPermission("invy.grant")) {
                sender.sendMessage(ChatColor.GRAY + messages.get("help.grant"));
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                // 権限チェック
                if (!sender.hasPermission("invy.reload")) {
                    sender.sendMessage(ChatColor.RED + messages.get("no_permission"));
                    return true;
                }

                // 言語再読み込み（変更があるかチェック）
                String oldLang = plugin.getMessageManager().getLang();
                plugin.reloadConfig(); // 設定ファイル再読み込み
                plugin.getMessageManager().loadMessages();
                String newLang = plugin.getMessageManager().getLang();

                // アイテム再読み込み
                ItemManager.LoadResult result = plugin.getItemManager().loadItems();

                // メッセージ送信
                sender.sendMessage(ChatColor.GREEN + plugin.getMessageManager().getFormatted("reload.success", Map.of(
                        "success", String.valueOf(result.success()),
                        "fail", String.valueOf(result.fail())
                )));

                if (!oldLang.equals(newLang)) {
                    sender.sendMessage(ChatColor.YELLOW + plugin.getMessageManager().getFormatted("reload.lang_changed", Map.of(
                            "oldLang", oldLang,
                            "newLang", newLang
                    )));
                }
                return true;
            }

            case "gui" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + messages.get("only_player"));
                    return true;
                }
                // 権限チェック
                if (!player.hasPermission("invy.use")) {
                    player.sendMessage(ChatColor.RED + messages.get("no_permission"));
                    return true;
                }
                plugin.getInvyGUI().open(player);
                return true;
            }

            case "grant" -> {
                // 権限チェック
                if (!sender.hasPermission("invy.grant")) {
                    sender.sendMessage(ChatColor.RED + messages.get("no_permission"));
                    return true;
                }

                // 引数チェック
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + messages.get("grant.usage"));
                    return true;
                }

                // プレイヤーがオンラインか確認
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + messages.get("player.not_found"));
                    return true;
                }

                try {
                    // コアの grant 処理へ委譲
                    return grantHandler.handleGrant(sender, args);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + messages.get("grant.invalid_time"));
                }
                return true;
            }

            default -> {
                sender.sendMessage(ChatColor.RED + messages.get("invalid_subcommand"));
                return true;
            }
        }
    }

    // 時間文字列を秒に変換する
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