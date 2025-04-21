package jp.co.epsilonlabs.invy.util;

import jp.co.epsilonlabs.invy.InvyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessageManager {

    private final InvyPlugin plugin;
    private FileConfiguration messages;

    public MessageManager(InvyPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    // メッセージファイルを読み込む
    public void loadMessages() {
        String lang = plugin.getConfig().getString("lang", "en").toLowerCase();

        switch (lang) {
            case "ja" -> loadFromInternal("message_ja.yml");
            case "custom" -> loadFromCustom();
            default -> loadFromInternal("message_en.yml");
        }
    }

    // 内部リソースから読み込む
    private void loadFromInternal(String fileName) {
        try (InputStream is = plugin.getResource(fileName)) {
            if (is == null) throw new FileNotFoundException("Resource not found: " + fileName);
            this.messages = YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().warning("[Invy] Failed to load internal message file: " + e.getMessage());
        }
    }

    // message.yml から読み込む
    private void loadFromCustom() {
        File file = new File(plugin.getDataFolder(), "message.yml");
        if (!file.exists()) {
            // 初回起動時のみ生成（上書きしない）
            plugin.saveResource("message.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(file);
    }

    // メッセージ取得
    public String get(String key) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key, "§c<missing:" + key + ">"));
    }

    // プレースホルダーを含むメッセージ取得
    public String getFormatted(String key, Map<String, String> replacements) {
        String msg = get(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return msg;
    }

    // 現在の言語取得
    public String getLang() {
        return plugin.getConfig().getString("lang", "en");
    }
}