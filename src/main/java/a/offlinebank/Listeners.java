package a.offlinebank;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;

import java.sql.*;

import static a.offlinebank.Offlinebank.plugin;
import static a.offlinebank.Offlinebank.prefix;

public class Listeners implements Listener {
    public Listeners() throws SQLException{
    }
    Connection con = DriverManager.getConnection(
            plugin.getConfig().getString("db.url"),
            plugin.getConfig().getString("db.user"),
            plugin.getConfig().getString("db.password")
    );
    @EventHandler
    public void onPlayerInteractevent(PlayerInteractEvent event) throws SQLException {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.OAK_SIGN) {
                Sign sign = (Sign) event.getClickedBlock().getState();
            }
        }
    }
}
