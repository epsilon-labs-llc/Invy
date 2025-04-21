package jp.co.epsilonlabs.invy;
import jp.co.epsilonlabs.invy.commands.InvyCommand;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public final class InvyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Invy enabled!");

        // コマンド登録
        Objects.requireNonNull(getCommand("invy")).setExecutor(new InvyCommand(this));
        Objects.requireNonNull(getCommand("invy")).setTabCompleter(new InvyCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Invy disabled.");
    }
}
