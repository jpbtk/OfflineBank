package a.offlinebank;

import a.offlinebank.Commands.OB;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import a.offlinebank.Offlinebank;

import java.sql.SQLException;

public final class Offlinebank extends JavaPlugin {

    public static JavaPlugin plugin;
    private Listeners listeners;
    public static Economy econ = null;
    public static String prefix = "§6[§e§lOfflineBank§6]§r ";
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
        if(!setupEconomy()){
            getLogger().severe("Vaultが見つかりませんでした。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Offlinebank has been disabled!");
        super.onDisable();
    }

    private static Boolean setupEconomy() {
        if(getPlugin().getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
            return false;
        }else{
            econ = rsp.getProvider();
        }
        return econ != null;
    }
    public static JavaPlugin getPlugin() {
        return plugin;
    }
}