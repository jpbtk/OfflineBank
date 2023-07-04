package a.offlinebank.Commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

import java.sql.*;

import static a.offlinebank.Offlinebank.econ;
import static a.offlinebank.Offlinebank.plugin;
import static a.offlinebank.Offlinebank.prefix;
import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class OB implements CommandExecutor, TabCompleter {
    private String[] completeList = new String[]{"help", "open", "deposit", "withdraw", "account", "balance"};

    public OB() throws SQLException {
    }

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
            e.sendMessage(prefix + "§a/ob help §7- §bこのヘルプを表示します。");
            e.sendMessage(prefix + "§a/ob open §7- §bオフラインバンクを開きます。");
            e.sendMessage(prefix + "§a/ob deposit <金額> §7- §bオフラインバンクにお金を預けます。");
            e.sendMessage(prefix + "§a/ob withdraw <金額> §7- §bオフラインバンクからお金を引き出します。");
            e.sendMessage(prefix + "§a/ob account §7- §bオフラインバンクの口座を開設します。");
            e.sendMessage(prefix + "§a/ob balance §7- §bオフラインバンクの残高を確認します。");
            return true;
        } else if (args[0].equals("adminhelp")) {
            e.sendMessage(prefix + "§a/ob help §7- §bユーザー用ヘルプを表示します。");
            e.sendMessage(prefix + "§a/ob open §7- §bオフラインバンクを開きます。");
            e.sendMessage(prefix + "§a/ob deposit <金額> §7- §bオフラインバンクにお金を預けます。");
            e.sendMessage(prefix + "§a/ob withdraw <金額> §7- §bオフラインバンクからお金を引き出します。");
            e.sendMessage(prefix + "§a/ob account §7- §bオフラインバンクの口座を開設します。");
            e.sendMessage(prefix + "§a/ob balance §7- §bオフラインバンクの残高を確認します。");
            e.sendMessage(prefix + "§a/ob adminhelp §7- §bこのヘルプを表示します。");
            e.sendMessage(prefix + "§a/ob give <プレイヤー> <金額> §7- §bプレイヤーのオフラインバンクにお金を預けます。");
            e.sendMessage(prefix + "§a/ob take <プレイヤー> <金額> §7- §bプレイヤーのオフラインバンクからお金を引き出します。");
            e.sendMessage(prefix + "§a/ob set <プレイヤー> §7- §bプレイヤーのオフラインバンクの口座を設定します。");
            e.sendMessage(prefix + "§a/ob account create <プレイヤー> §7- §bプレイヤーのオフラインバンクの口座を開設します。");
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
                    econ.depositPlayer(e, amount);
                    try {
                        PreparedStatement ps = con.prepareStatement("SELECT * FROM offlinebank WHERE uuid = ?");
                        ps.setString(1, e.getUniqueId().toString());
                        ResultSet rs = ps.executeQuery();
                        if (!rs.next()) {
                            e.sendMessage(prefix + "§cあなたはオフラインバンクに登録されていません。");
                            return true;
                        } else {
                            int balance = rs.getInt("balance");
                            int newbalance = balance - amount;
                            if (newbalance < 0) {
                                e.sendMessage(prefix + "§cお金が足りません。");
                                return true;
                            } else {
                                PreparedStatement ps2 = con.prepareStatement("UPDATE offlinebank SET balance = ? WHERE uuid = ?");
                                ps2.setInt(1, newbalance);
                                ps2.setString(2, e.getUniqueId().toString());
                                ps2.executeUpdate();
                                e.sendMessage(prefix + "§a" + amount + "円引き出しました。");
                                return true;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
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
                            PreparedStatement ps = con.prepareStatement("UPDATE offlinebank SET balance = balance + ? WHERE uuid = ?");
                            ps.setInt(1, amount);
                            ps.setString(2, e.getUniqueId().toString());
                            ps.executeUpdate();
                            e.sendMessage(prefix + "§e" + amount + "円入金がありました。");
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            }
            return false;
        } else if (args[0].equals("account")) {
            if (args.length == 1) {
                e.sendMessage(prefix + "§c引数が不足しています。");
                e.sendMessage(prefix + "§c/ob account <プレイヤー名> で実行します。");
                return true;
            } else if (args[1].equals("create")) {
                if (!e.hasPermission("ob.admin")) {
                    e.sendMessage(prefix + "§cあなたには権限がありません。");
                    return true;
                }else {
                    if (args.length == 2) {
                        e.sendMessage(prefix + "§c引数が不足しています。");
                        e.sendMessage(prefix + "§c/ob account create <プレイヤー名> で実行します。");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                        return true;
                    }
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO offlinebank (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = balance + ?");
                        ps.setString(1, target.getUniqueId().toString());
                        ps.setInt(2, 0);
                        ps.setInt(3, 0);
                        ps.executeUpdate();
                        ps.close();
                        e.sendMessage(prefix + "§a" + target.getName() + "の口座を作成しました。");
                    } catch (SQLException ex) {
                        e.sendMessage(prefix + "§cエラーが発生しました。");
                        throw new RuntimeException(ex);
                    }
                }
            } else if (args[1].equals("delete")) {
                if (!e.hasPermission("ob.admin")) {
                    e.sendMessage(prefix + "§cあなたには権限がありません。");
                    return true;
                }else {
                    if (args.length == 2) {
                        e.sendMessage(prefix + "§c引数が不足しています。");
                        e.sendMessage(prefix + "§c/ob account delete <プレイヤー名> で実行します。");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                        return true;
                    }
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM offlinebank WHERE uuid = ?");
                        ps.setString(1, target.getUniqueId().toString());
                        ps.executeUpdate();
                        ps.close();
                        e.sendMessage(prefix + "§a" + target.getName() + "の口座を削除しました。");
                    } catch (SQLException ex) {
                        e.sendMessage(prefix + "§cエラーが発生しました。");
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                    return true;
                }
                try {
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM offlinebank WHERE uuid = ?");
                    ps.setString(1, target.getUniqueId().toString());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        e.sendMessage(prefix + "§e----- §6§l" + target.getName() + "の口座 §e-----");
                        e.sendMessage(prefix + "§e残高: §f" + rs.getInt("balance") + "円");
                    } else {
                        e.sendMessage(prefix + "§c" + target.getName() + "の口座は存在しません。");
                    }
                    ps.close();
                    rs.close();
                } catch (SQLException ex) {
                    e.sendMessage(prefix + "§cエラーが発生しました。");
                    throw new RuntimeException(ex);
                }
            }
            return true;
        } else if (args[0].equals("give")) {
            if (sender.hasPermission("admin")) {
                if (args.length == 1) {
                    e.sendMessage(prefix + "§c引数が不足しています。");
                    e.sendMessage(prefix + "§c/ob give <プレイヤー名> <金額> で実行します。");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                    return true;
                }
                if (isNumber(args[2])) {
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        e.sendMessage(prefix + "§c引数が不正です。");
                        e.sendMessage(prefix + "§c/ob give <プレイヤー名> <金額> で実行します。");
                        return true;
                    } else {
                        try {
                            PreparedStatement ps = con.prepareStatement("UPDATE offlinebank SET balance = balance + ? WHERE uuid = ?");
                            ps.setInt(1, amount);
                            ps.setString(2, target.getUniqueId().toString());
                            ps.executeUpdate();
                            e.sendMessage(prefix + "§a" + target.getName() + "に" + amount + "円与えました。");
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            } else {
                e.sendMessage(prefix + "§c権限がありません。");
                return true;
            }
        } else if (args[0].equals("take")) {
            if (sender.hasPermission("admin")) {
                if (args.length == 1) {
                    e.sendMessage(prefix + "§c引数が不足しています。");
                    e.sendMessage(prefix + "§c/ob take <プレイヤー名> <金額> で実行します。");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                    return true;
                }
                if (isNumber(args[2])) {
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        e.sendMessage(prefix + "§c引数が不正です。");
                        e.sendMessage(prefix + "§c/ob take <プレイヤー名> <金額> で実行します。");
                        return true;
                    } else {
                        try {
                            PreparedStatement ps = con.prepareStatement("SELECT * FROM offlinebank WHERE uuid = ?");
                            ps.setString(1, target.getUniqueId().toString());
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                if (rs.getInt("balance") < amount) {
                                    e.sendMessage(prefix + "§c" + target.getName() + "の残高が足りません。");
                                    return true;
                                } else {
                                    ps = con.prepareStatement("UPDATE offlinebank SET balance = balance - ? WHERE uuid = ?");
                                    ps.setInt(1, amount);
                                    ps.setString(2, target.getUniqueId().toString());
                                    ps.executeUpdate();
                                    e.sendMessage(prefix + "§a" + target.getName() + "から" + amount + "円取りました。");
                                }
                            } else {
                                e.sendMessage(prefix + "§c" + target.getName() + "の口座は存在しません。");
                            }
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            } else {
                e.sendMessage(prefix + "§c権限がありません。");
                return true;
            }
        } else if (args[0].equals("set")) {
            if (sender.hasPermission("admin")) {
                if (args.length == 1) {
                    e.sendMessage(prefix + "§c引数が不足しています。");
                    e.sendMessage(prefix + "§c/ob set <プレイヤー名> <金額> で実行します。");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                    return true;
                }
                if (isNumber(args[2])) {
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        e.sendMessage(prefix + "§c引数が不正です。");
                        e.sendMessage(prefix + "§c/ob set <プレイヤー名> <金額> で実行します。");
                        return true;
                    } else {
                        try {
                            PreparedStatement ps = con.prepareStatement("UPDATE offlinebank SET balance = ? WHERE uuid = ?");
                            ps.setInt(1, amount);
                            ps.setString(2, target.getUniqueId().toString());
                            ps.executeUpdate();
                            e.sendMessage(prefix + "§a" + target.getName() + "の残高を" + amount + "円に設定しました。");
                        } catch (SQLException ex) {
                            e.sendMessage(prefix + "§cエラーが発生しました。");
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return false;
            } else {
                e.sendMessage(prefix + "§c権限がありません。");
                return true;
            }
        } else if (args[0].equals("reload")) {
            if (sender.hasPermission("admin")) {
                plugin.reloadConfig();
                e.sendMessage(prefix + "§aconfigを再読み込みしました。");
                return true;
            } else {
                e.sendMessage(prefix + "§c権限がありません。");
                return true;
            }
        }else if (args[0].equals("bal")) {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM offlinebank WHERE uuid = ?");
                ps.setString(1, e.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                e.sendMessage(prefix + "§6=====§e§l" + e.getName() + "§6の残高=====");
                e.sendMessage(prefix + "§eVault: §a" + econ.getBalance(e));
                if (rs.next()) {
                    e.sendMessage(prefix + "§eOfflineBank: §a" + rs.getInt("balance"));
                }
                e.sendMessage(prefix + "§6====================");
            } catch (SQLException ex) {
                e.sendMessage(prefix + "§cエラーが発生しました。");
                throw new RuntimeException(ex);
            }
        }else if (args[0].equals("pay")){
            if (args.length == 1) {
                e.sendMessage(prefix + "§c引数が不足しています。");
                e.sendMessage(prefix + "§c/ob pay <プレイヤー名> <金額> で実行します。");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                e.sendMessage(prefix + "§cプレイヤーが見つかりませんでした。");
                return true;
            }
            if (isNumber(args[2])) {
                int amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    e.sendMessage(prefix + "§c引数が不正です。");
                    e.sendMessage(prefix + "§c/ob pay <プレイヤー名> <金額> で実行します。");
                    return true;
                } else {
                    try {
                        PreparedStatement ps = con.prepareStatement("SELECT * FROM offlinebank WHERE uuid = ?");
                        ps.setString(1, e.getUniqueId().toString());
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            if (rs.getInt("balance") < amount) {
                                e.sendMessage(prefix + "§c残高が足りません。");
                                return true;
                            } else {
                                ps = con.prepareStatement("UPDATE offlinebank SET balance = balance - ? WHERE uuid = ?");
                                ps.setInt(1, amount);
                                ps.setString(2, e.getUniqueId().toString());
                                ps.executeUpdate();
                                ps = con.prepareStatement("UPDATE offlinebank SET balance = balance + ? WHERE uuid = ?");
                                ps.setInt(1, amount);
                                ps.setString(2, target.getUniqueId().toString());
                                ps.executeUpdate();
                                e.sendMessage(prefix + "§e§l" + target.getName() + "§eに§e§l" + amount + "円送りました！");
                                target.sendMessage(prefix + "§e§l" + e.getName() + "§eから§e§l" + amount + "円受け取りました！");
                            }
                        } else {
                            e.sendMessage(prefix + "§cあなたの口座は存在しません。");
                        }
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("ob")) {
            if (args.length == 1) {
                List<String> tab = new ArrayList<>();
                tab.add("withdraw");
                tab.add("deposit");
                tab.add("account");
                tab.add("help");
                if (sender.hasPermission("admin")) {
                    tab.add("adminhelp");
                    tab.add("give");
                    tab.add("take");
                    tab.add("set");
                    tab.add("reload");
                }
                return tab;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("account")) {
                    List<String> tab = new ArrayList<>();
                    tab.add("create");
                    tab.add("delete");
                    tab.add(plugin.getServer().getPlayer(args[1]).getName());
                    return tab;
                } else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("deposit")) {
                    List<String> tab = new ArrayList<>();
                    tab.add("<金額>");
                    return tab;
                }
            }else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("set")) {
                    List<String> tab = new ArrayList<>();
                    tab.add("<金額>");
                    return tab;
                }
            }
        }
        return null;
    }

    Connection con = DriverManager.getConnection(
            plugin.getConfig().getString("db.url"),
            plugin.getConfig().getString("db.user"),
            plugin.getConfig().getString("db.password")
    );
}