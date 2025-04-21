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

import java.util.Map;
import java.util.Objects;

public class InventoryClickListener implements Listener {

    private final InvyPlugin plugin;
    private final String guiTitle; // GUIのタイトル

    public InventoryClickListener(InvyPlugin plugin) {
        this.plugin = plugin;
        this.guiTitle = ChatColor.BLUE + plugin.getMessageManager().get("item.list_title");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MessageManager messages = plugin.getMessageManager();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!title.equals(guiTitle)) return;

        // GUI内のすべてのクリックを無効化
        event.setCancelled(true);
        event.setResult(org.bukkit.event.Event.Result.DENY);

        // GUIの中をクリックしていなければ無視
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        // アイテム名取得
        String itemName = clicked.hasItemMeta() && Objects.requireNonNull(clicked.getItemMeta()).hasDisplayName()
                ? clicked.getItemMeta().getDisplayName()
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
        String viewTitle = event.getView().getTitle();
        if (viewTitle.equals(guiTitle)) {
            event.setCancelled(true);
        }
    }
}