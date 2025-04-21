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

import java.util.ArrayList;
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

    public void loadItems() {
        items.clear();

        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) {
            plugin.saveResource("items.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("items");

        if (section == null) {
            plugin.getLogger().warning("items.yml に items セクションが見つかりません。");
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                ConfigurationSection itemSection = section.getConfigurationSection(key);
                if (itemSection == null) continue;

                Material material = Material.valueOf(itemSection.getString("material", "STONE").toUpperCase());
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                // 表示名の設定
                String name = itemSection.getString("name", "");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

                // エンチャント & 説明文
                List<String> lore = new ArrayList<>();
                ConfigurationSection enchants = itemSection.getConfigurationSection("enchants");
                if (enchants != null) {
                    for (String ench : enchants.getKeys(false)) {
                        NamespacedKey keyNs = NamespacedKey.minecraft(ench.toLowerCase());
                        Enchantment enchant = Enchantment.getByKey(keyNs);
                        int level = enchants.getInt(ench);
                        if (enchant != null) {
                            meta.addEnchant(enchant, Math.min(level, 999), true);
                            lore.add(ChatColor.GRAY + enchant.getKey().getKey().toUpperCase() + " " + level);
                        }
                    }
                    meta.setLore(lore);
                }

                // 耐久設定
                meta.setUnbreakable(itemSection.getBoolean("unbreakable", false));

                // 属性表示を非表示に
                meta.addItemFlags(
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_UNBREAKABLE
                );

                // NBTタグ追加
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_item"), PersistentDataType.BYTE, (byte) 1);

                // メタ情報をアイテムに適用
                item.setItemMeta(meta);
                items.put(id, item);
            } catch (Exception e) {
                plugin.getLogger().warning("アイテム ID " + key + " の読み込みに失敗しました: " + e.getMessage());
            }
        }

        plugin.getLogger().info("合計 " + items.size() + " 個のカスタムアイテムを読み込みました。");
    }

    public Map<Integer, ItemStack> getAllItems() {
        return items;
    }
}