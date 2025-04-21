package jp.co.epsilonlabs.invy.gui;

import jp.co.epsilonlabs.invy.InvyPlugin;
import jp.co.epsilonlabs.invy.item.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InvyGUI {

    private final InvyPlugin plugin;

    public InvyGUI(InvyPlugin plugin) {
        this.plugin = plugin;
    }

    // GUIを開く処理
    public void open(Player player) {
        String title = ChatColor.BLUE + plugin.getMessageManager().get("item.list_title");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        ItemManager itemManager = plugin.getItemManager();
        for (Map.Entry<Integer, ItemStack> entry : itemManager.getAllItems().entrySet()) {
            int slot = entry.getKey() - 1;
            if (slot >= 0 && slot < 54) {
                gui.setItem(slot, entry.getValue());
            }
        }

        player.openInventory(gui);
    }
}