package jp.co.epsilonlabs.invy.listener;

import jp.co.epsilonlabs.invy.InvyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class ItemInventoryListener implements Listener {

    private final InvyPlugin plugin;

    public ItemInventoryListener(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemInventory(InventoryClickEvent event) {
        // 設定ファイルで無効化されていればスキップ
        if (!plugin.getConfig().getBoolean("features.prevent_storage", false)) return;

        // プレイヤーか確認
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // 操作中のアイテムを取得
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        // アイテム内のメタデータの取得
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // カスタムアイテムかどうか判定
        NamespacedKey key = new NamespacedKey(plugin, "invy_item");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        // 保管先のインベントリタイプを取得
        InventoryType type = event.getView().getTopInventory().getType();

        // エンダーチェストは許可
        if (type == InventoryType.ENDER_CHEST) return;

        // チェスト、シェルカーボックス、樽は禁止
        if (type == InventoryType.CHEST || type == InventoryType.SHULKER_BOX || type == InventoryType.BARREL) {
            event.setCancelled(true);
        }
    }
}