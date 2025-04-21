package jp.co.epsilonlabs.invy.listener;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.util.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class InventoryClickListener implements Listener {

    private final InvyPlugin plugin;

    public InventoryClickListener(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MessageManager messages = plugin.getMessageManager();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String expectedTitle = ChatColor.BLUE + plugin.getMessageManager().get("item.list_title");
        if (!ChatColor.stripColor(title).equals(ChatColor.stripColor(expectedTitle))) return;

        // GUI内のすべてのクリックを無効化
        event.setCancelled(true);

        // GUIの外（自分のインベントリ）をクリックした場合は無視
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        // アイテム名取得
        ItemMeta meta = clicked.getItemMeta();
        String itemName = (meta != null && meta.hasDisplayName())
                ? meta.getDisplayName()
                : clicked.getType().name();

        // インベントリに空きがあるか確認してから追加
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(clicked.clone());
            player.sendMessage(ChatColor.GREEN + messages.getFormatted("gui.received", Map.of("item", itemName)));
        } else {
            // 空きがない場合の警告メッセージ
            player.sendMessage(ChatColor.RED + messages.get("inventory.full"));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        String expectedTitle = ChatColor.BLUE + plugin.getMessageManager().get("item.list_title");
        if (!ChatColor.stripColor(title).equals(ChatColor.stripColor(expectedTitle))) return;

        // GUIの中のスロットにドラッグされていたらキャンセル
        if (event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize())) {
            event.setCancelled(true);
        }
    }
}