package jp.co.epsilonlabs.invy.listener;

import jp.co.epsilonlabs.invy.InvyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;


public class ItemDropListener implements Listener {

    private final InvyPlugin plugin;

    public ItemDropListener(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // 設定ファイルで無効化されていればスキップ
        if (!plugin.getConfig().getBoolean("features.prevent_drop", false)) return;

        // 操作中のアイテムを取得
        ItemStack item = event.getItemDrop().getItemStack();
        if (!item.hasItemMeta()) return;

        // アイテム内のメタデータの取得
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // カスタムアイテムかどうか判定
        NamespacedKey key = new NamespacedKey(plugin, "invy_item");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        // アイテムを削除（ドロップ無効）
        event.getItemDrop().remove();
    }
}