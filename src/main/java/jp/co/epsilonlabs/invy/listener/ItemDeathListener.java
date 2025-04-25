package jp.co.epsilonlabs.invy.listener;

import jp.co.epsilonlabs.invy.InvyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.Iterator;

public class ItemDeathListener implements Listener {

    private final InvyPlugin plugin;

    public ItemDeathListener(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // 設定で無効化されている場合はスキップ
        if (!plugin.getConfig().getBoolean("features.prevent_death_drop", true)) return;

        // ドロップ予定アイテムを走査して InvItem なら除外
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            // アイテムを取得
            ItemStack item = iterator.next();
            if (item == null || !item.hasItemMeta()) continue;

            // アイテム内のメタデータの取得
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            // カスタムアイテムかどうか判定
            NamespacedKey key = new NamespacedKey(plugin, "invy_item");
            if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) continue;

            // アイテムを削除（ドロップ無効）
            iterator.remove();
        }
    }
}
