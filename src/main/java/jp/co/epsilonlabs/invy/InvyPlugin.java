package jp.co.epsilonlabs.invy;
import jp.co.epsilonlabs.invy.commands.InvyCommand;
import jp.co.epsilonlabs.invy.item.ItemManager;
import jp.co.epsilonlabs.invy.gui.InvyGUI;
import jp.co.epsilonlabs.invy.listener.InventoryClickListener;
import jp.co.epsilonlabs.invy.listener.ItemDropListener;
import jp.co.epsilonlabs.invy.util.MessageManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

public final class InvyPlugin extends JavaPlugin {

    private ItemManager itemManager;
    private InvyGUI invyGUI;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Invy enabled!");

        // config.yml, items.yml をロード
        saveDefaultConfig();
        reloadConfig();

        // メッセージマネージャー初期化
        this.messageManager = new MessageManager(this);

        // アイテムマネージャーの初期化
        this.itemManager = new ItemManager(this);
        itemManager.loadItems();

        // GUIの初期化
        invyGUI = new InvyGUI(this);

        // リスナーの登録
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryClickListener(this), this);
        pluginManager.registerEvents(new ItemDropListener(this), this);

        // コマンドの登録
        InvyCommand commandHandler = new InvyCommand(this);
        PluginCommand command = Objects.requireNonNull(getCommand("invy"));
        command.setExecutor(commandHandler);
        command.setTabCompleter(commandHandler);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Invy disabled.");
    }

    // カスタムアイテムを管理する ItemManager を返す
    public ItemManager getItemManager() {
        return itemManager;
    }

    // GUIを操作するための InvyGUI を返す
    public InvyGUI getInvyGUI() {
        return invyGUI;
    }

    //多言語のための MessageManager を返す
    public MessageManager getMessageManager() {
        return messageManager;
    }
}
