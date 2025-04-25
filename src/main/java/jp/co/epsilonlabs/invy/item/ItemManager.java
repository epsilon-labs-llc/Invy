package jp.co.epsilonlabs.invy.item;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.util.MessageManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

            // 耐久無限の設定
            meta.setUnbreakable(itemSection.getBoolean("unbreakable", false));

            // エンチャント
            ConfigurationSection enchants = itemSection.getConfigurationSection("enchants");
            if (enchants != null) {
                for (String ench : enchants.getKeys(false)) {
                    Enchantment enchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(ench.toLowerCase()));
                    int level = enchants.getInt(ench);
                    if (enchant != null) {
                        meta.addEnchant(enchant, Math.min(level, 999), true);
                    }
                }
            }

            // 属性
            ConfigurationSection attributes = itemSection.getConfigurationSection("attributes");
            if (attributes != null) {
                for (String attr : attributes.getKeys(false)) {
                    // 属性の取得
                    Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(attr.toLowerCase()));
                    if (attribute == null) {
                        Map<String, String> vars = Map.of("attribute", attr);
                        plugin.getLogger().warning(messages.getFormatted("item.attribute.not_found", vars));
                        continue;
                    }

                    // 属性セクションの取得
                    ConfigurationSection attrSection = attributes.getConfigurationSection(attr);
                    if (attrSection == null) {
                        Map<String, String> vars = Map.of("attribute", attr);
                        plugin.getLogger().warning(messages.getFormatted("item.attribute.section_invalid", vars));
                        continue;
                    }

                    // 値の取得
                    NamespacedKey key = new NamespacedKey(plugin, attr.toLowerCase());
                    double amount = attrSection.getDouble("amount");
                    String opStr = attrSection.getString("operation", "ADD_NUMBER").toUpperCase();
                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(opStr);

                    // スロットの取得
                    String slotStr = attrSection.getString("slots", "ANY").toUpperCase();
                    EquipmentSlotGroup slotGroup;
                    try {
                        slotGroup = EquipmentSlotGroup.getByName(slotStr);
                    } catch (IllegalArgumentException e) {
                        Map<String, String> vars = Map.of("slot", slotStr, "attribute", attr);
                        plugin.getLogger().warning(messages.getFormatted("item.attribute.invalid_slot", vars));
                        slotGroup = EquipmentSlotGroup.ANY;
                    }

                    // 属性の追加
                    AttributeModifier modifier = new AttributeModifier(key, amount, operation, slotGroup);
                    meta.addAttributeModifier(attribute, modifier);
                }
            }

            // ポーション効果
            ConfigurationSection potion_effects = itemSection.getConfigurationSection("potion_effects");
            if (potion_effects != null && (material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION)) {
                // PotionMeta にキャスト
                if (!(meta instanceof PotionMeta potionMeta)) {
                    plugin.getLogger().warning(messages.get("item.potion_effect.meta_invalid"));
                    continue;
                }

                for (String potion_effect : potion_effects.getKeys(false)) {
                    // ポーション効果の取得
                    PotionEffectType type = PotionEffectType.getByName(potion_effect.toUpperCase());
                    if (type == null) {
                        Map<String, String> vars = Map.of("potion_effect", potion_effect);
                        plugin.getLogger().warning(messages.getFormatted("item.potion_effect.not_found", vars));
                        continue;
                    }

                    // ポーション効果セクションの取得
                    int durationSeconds = potion_effects.getInt(potion_effect + ".duration", 0);
                    int level = potion_effects.getInt(potion_effect + ".level", 0);
                    boolean ambient = potion_effects.getBoolean(potion_effect + ".ambient", false);
                    boolean particles = potion_effects.getBoolean(potion_effect + ".particles", true);
                    boolean icon = potion_effects.getBoolean(potion_effect + ".icon", true);

                    // tick変換（負なら "無限" として Integer.MAX_VALUE に）
                    int durationTicks = (durationSeconds < 0) ? Integer.MAX_VALUE : durationSeconds * 20;

                    // 効果の追加
                    PotionEffect effect = new PotionEffect(type, durationTicks, level, ambient, particles, icon);
                    potionMeta.addCustomEffect(effect, true);
                }

                // カスタムアイテム識別用のNBTフラグを設定
                NamespacedKey key = new NamespacedKey(plugin, "custom_item");
                potionMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

                // メタ情報をポーションに適用
                item.setItemMeta(potionMeta);
            } else {
                // 通常アイテム
                // アイテムのエンチャント・属性表示を隠す
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
            }
            // アイテムのエンチャント・属性表示を隠す
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

            // アイテム登録
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