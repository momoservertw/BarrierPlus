package tw.momocraft.barrierplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.utils.PlayerPointsAPI;

import java.util.Collection;
import java.util.UUID;

public class PlayerHandler {

    public static Player getPlayerString(String playerName) {
        Player args = null;
        try {
            args = Bukkit.getPlayer(UUID.fromString(playerName));
        } catch (Exception ignored) {
        }
        if (args == null) {
            return Bukkit.getPlayer(playerName);
        }
        return args;
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        Collection<?> playersOnlineNew;
        OfflinePlayer[] playersOnlineOld;
        try {
            if (Bukkit.class.getMethod("getOfflinePlayers").getReturnType() == Collection.class) {
                playersOnlineNew = ((Collection<?>) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                for (Object objPlayer : playersOnlineNew) {
                    Player player = ((Player) objPlayer);
                    if (player.getName().equalsIgnoreCase(playerName)) {
                        return player;
                    }
                }
            } else {
                playersOnlineOld = ((OfflinePlayer[]) Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                for (OfflinePlayer player : playersOnlineOld) {
                    if (player.getName().equalsIgnoreCase(playerName)) {
                        return player;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static double getTypeBalance(Player player, String priceType) {
        if (priceType.equals("points") && ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            return playerPointsAPI.getPoints(player);
        } else if (priceType.equals("money") && ConfigHandler.getDepends().VaultEnabled() && ConfigHandler.getDepends().getVaultApi().getEconomy() != null) {
            return ConfigHandler.getDepends().getVaultApi().getEconomy().getBalance(player);
        }
        return 0;
    }

    public static double takeTypeMoney(Player player, String priceType, int price) {
        if (priceType.equals("points") && ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            return playerPointsAPI.takePoints(player, price);
        } else if (priceType.equals("money") && ConfigHandler.getDepends().VaultEnabled() && ConfigHandler.getDepends().getVaultApi().getEconomy() != null) {
            ConfigHandler.getDepends().getVaultApi().getEconomy().withdrawPlayer(player, price);
            return ConfigHandler.getDepends().getVaultApi().getEconomy().getBalance(player);
        }
        return 0;
    }
}