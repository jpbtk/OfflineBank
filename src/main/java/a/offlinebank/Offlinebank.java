package a.offlinebank;

import a.offlinebank.Commands.OB;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Offlinebank extends JavaPlugin {

    public static JavaPlugin plugin;
    private Listeners listeners;
    public static Economy econ = null;
    public static String prefix = "§7[§eOfflinebank§7]§r ";
    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Offlinebank has been enabled!");
        try{
            this.listeners = new Listeners();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        Bukkit.getPluginManager().registerEvents((Listener) this.listeners, this);
        try {
            getCommand("ob").setExecutor(new OB());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        plugin.saveDefaultConfig();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        getLogger().info("Offlinebank has been disabled!");
        super.onDisable();
    }
    public static JavaPlugin getPlugin() {
        return plugin;
    }
}