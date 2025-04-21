package jp.co.epsilonlabs.invy.item;

import jp.co.epsilonlabs.invy.InvyPlugin;
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

    public LoadResult loadItems() {
        // 既存のアイテムリストを初期化
        items.clear();

        // 成功 / 失敗の件数カウント
        int success = 0;
        int fail = 0;

        // items.yml ファイルを取得
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) {
            plugin.saveResource("items.yml", false);
        }

        // YAML構成ファイルとして読み込み
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        // items セクションを取得
        ConfigurationSection section = config.getConfigurationSection("items");

        // セクションが存在しない場合は警告ログを出して終了
        if (section == null) {
            plugin.getLogger().warning("items.yml に items セクションが見つかりません。");
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

            String rawMaterial = itemSection.getString("material", "STONE").toUpperCase();
            Material material;
            try {
                material = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("無効な素材です: " + rawMaterial + "（ID: " + id + "）");
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

            // 耐久設定
            meta.setUnbreakable(itemSection.getBoolean("unbreakable", false));

            // 属性表示を非表示に
            meta.addItemFlags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_UNBREAKABLE
            );

            // NBTにフラグを埋め込む
            NamespacedKey key = new NamespacedKey(plugin, "custom_item");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            // メタ情報をアイテムに適用
            item.setItemMeta(meta);
            items.put(id, item);
            success++;
        }

        plugin.getLogger().info("アイテム読み込み完了: 成功 " + success + " 件 / 失敗 " + fail + " 件");
        return new LoadResult(success, fail);
    }

    public Map<Integer, ItemStack> getAllItems() {
        return items;
    }

    public record LoadResult(int success, int fail) {}
}