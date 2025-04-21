package jp.co.epsilonlabs.invy.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryClickListener implements Listener {

    // GUIのタイトル
    private static final String GUI_TITLE = ChatColor.BLUE + "アイテム一覧";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!title.equals(GUI_TITLE)) return;

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
            player.sendMessage(ChatColor.GREEN + itemName + ChatColor.GREEN + " を取得しました！");
        } else {
            player.sendMessage(ChatColor.RED + "インベントリに空きがありません！");
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String viewTitle = event.getView().getTitle();
        if (viewTitle.equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}