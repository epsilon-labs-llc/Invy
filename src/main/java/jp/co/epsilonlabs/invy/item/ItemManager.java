package jp.co.epsilonlabs.invy.item;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.util.MessageManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class ItemManager {

    private final InvyPlugin plugin;
    private final Map<Integer, ItemStack> items = new HashMap<>();

    public ItemManager(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    // items.yml を読み込んでカスタムアイテムを生成する
    public LoadResult loadItems() {
        MessageManager messages = plugin.getMessageManager();

        // 既存のアイテムリストを初期化
        items.clear();

        int success = 0; // 読み込み成功数
        int fail = 0;    // 読み込み失敗数

        // items.yml ファイルを取得
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) {
            plugin.saveResource("items.yml", false); // 初回起動時に生成
        }

        // YAML構成ファイルとして読み込み
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        // items セクションを取得
        ConfigurationSection section = config.getConfigurationSection("items");

        // セクションが存在しない場合は警告ログを出して終了
        if (section == null) {
            plugin.getLogger().warning(messages.get("item.section_missing"));
            return new LoadResult(0, 1);
        }

        // IDを整数に変換して昇順に並べる
        List<Integer> ids = section.getKeys(false).stream()
                .map(Integer::parseInt)
                .sorted()
                .toList();

        // 各IDごとにアイテムを生成・登録
        for (int id : ids) {
            ConfigurationSection itemSection = section.getConfigurationSection(String.valueOf(id));
            if (itemSection == null) continue;

            // 素材を取得
            String rawMaterial = itemSection.getString("material", "STONE").toUpperCase();
            Material material;
            try {
                material = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException e) {
                Map<String, String> vars = Map.of(
                        "material", rawMaterial,
                        "id", String.valueOf(id)
                );
                plugin.getLogger().warning(messages.getFormatted("item.invalid_material", vars));
                fail++;
                continue;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            // 名前
            String name = itemSection.getString("name", null);
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            // 説明文（lore）
            if (itemSection.isList("lore")) {
                List<String> lore = itemSection.getStringList("lore").stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .toList();
                meta.setLore(lore);
            }

            // エンチャント
            ConfigurationSection enchants = itemSection.getConfigurationSection("enchants");
            if (enchants != null) {
                for (String ench : enchants.getKeys(false)) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(ench.toLowerCase()));
                    int level = enchants.getInt(ench);
                    if (enchant != null) {
                        meta.addEnchant(enchant, Math.min(level, 999), true);
                    }
                }
            }

            // 耐久無限の設定
            meta.setUnbreakable(itemSection.getBoolean("unbreakable", false));

            // アイテムの属性・エンチャント表示を隠す
            meta.addItemFlags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_UNBREAKABLE
            );

            // カスタムアイテム識別用のNBTフラグを設定
            NamespacedKey key = new NamespacedKey(plugin, "custom_item");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            // メタ情報をアイテムに適用
            item.setItemMeta(meta);
            items.put(id, item);
            success++;
        }

        Map<String, String> resultVars = Map.of(
                "success", String.valueOf(success),
                "fail", String.valueOf(fail)
        );
        plugin.getLogger().info(messages.getFormatted("item.load_summary", resultVars));
        return new LoadResult(success, fail);
    }

    // 読み込まれた全カスタムアイテムを返す
    public Map<Integer, ItemStack> getAllItems() {
        return items;
    }

    // 成功・失敗の件数を保持するレコードクラス
    public record LoadResult(int success, int fail) {}
}