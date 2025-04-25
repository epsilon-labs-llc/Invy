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
        ItemStack dropped = event.getItemDrop().getItemStack();

        // カスタムアイテムかを判定（NBTタグ 'invy_item' がついているか）
        if (dropped.hasItemMeta()) {
            ItemMeta meta = dropped.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "invy_item");

            if (meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                // タグ付きなら削除（ドロップ無効）
                event.getItemDrop().remove();
            }
        }
    }
}