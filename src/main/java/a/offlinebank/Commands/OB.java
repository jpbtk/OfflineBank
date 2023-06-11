package a.offlinebank.Commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static a.offlinebank.Offlinebank.econ;
import static a.offlinebank.Offlinebank.plugin;
import static a.offlinebank.Offlinebank.prefix;
import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class OB implements CommandExecutor {
    public OB() throws SQLException {
    }

    Connection con = DriverManager.getConnection(
            plugin.getConfig().getString("db.url"),
            plugin.getConfig().getString("db.user"),
            plugin.getConfig().getString("db.password")
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§cこのコマンドはプレイヤーのみ実行可能です。");
            return true;
        }
        Player e = (Player) sender;
        if (args.length == 0) {
            e.sendMessage(prefix + "§c引数が不足しています。");
            e.sendMessage(prefix + "§c/ob help でヘルプを表示します。");
            return true;
        } else if (args[0].equals("help")) {
            e.sendMessage(prefix + "§e----- §6§lOfflinebank Help §e-----");
            e.sendMessage(prefix + "§e/ob help §7- §fこのヘルプを表示します。");
            e.sendMessage(prefix + "§e/ob open §7- §fオフラインバンクを開きます。");
            e.sendMessage(prefix + "§e/ob deposit §7- §fオフラインバンクにお金を預けます。");
            e.sendMessage(prefix + "§e/ob withdraw §7- §fオフラインバンクからお金を引き出します。");
            e.sendMessage(prefix + "§e/ob balance §7- §fオフラインバンクの残高を確認します。");
            return true;
        } else if (args[0].equals("withdraw")) {
            if (args.length == 1) {
                e.sendMessage(prefix + "§c引数が不足しています。");
                e.sendMessage(prefix + "§c/ob withdraw <金額> で実行します。");
                return true;
            }
            if (isNumber(args[1])) {
                int amount = Integer.parseInt(args[1]);
                if (amount <= 0) {
                    e.sendMessage(prefix + "§c引数が不正です。");
                    e.sendMessage(prefix + "§c/ob withdraw <金額> で実行します。");
                    return true;
                } else {
                    if (econ.getBalance(e) < amount) {
                        e.sendMessage(prefix + "§cお金が足りません。");
                        return true;
                    } else {
                        econ.withdrawPlayer(e, amount);
                        try {
                            PreparedStatement ps = con.prepareStatement("UPDATE ob SET balance = balance + ? WHERE uuid = ?");
                            ps.setInt(1, amount);
                            ps.setString(2, e.getUniqueId().toString());
                            ps.executeUpdate();
                            ps.close();
                            e.sendMessage(prefix + "§a" + amount + "円引き出しました。");
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            }
            return false;
        } else if (args[0].equals("deposit")) {
            if (args.length == 1) {
                e.sendMessage(prefix + "§c引数が不足しています。");
                e.sendMessage(prefix + "§c/ob deposit <金額> で実行します。");
                return true;
            }
            if (isNumber(args[1])) {
                int amount = Integer.parseInt(args[1]);
                if (amount <= 0) {
                    e.sendMessage(prefix + "§c引数が不正です。");
                    e.sendMessage(prefix + "§c/ob deposit <金額> で実行します。");
                    return true;
                } else {
                    if (econ.getBalance(e) < amount) {
                        e.sendMessage(prefix + "§cお金が足りません。");
                        return true;
                    } else {
                        econ.withdrawPlayer(e, amount);
                        try {
                            PreparedStatement ps = con.prepareStatement("UPDATE ob SET balance = balance + ? WHERE uuid = ?");
                            ps.setInt(1, amount);
                            ps.setString(2, e.getUniqueId().toString());
                            ps.executeUpdate();
                            ps.close();
                            e.sendMessage(prefix + "§a" + amount + "円預けました。");
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }
}