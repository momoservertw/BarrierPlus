package tw.momocraft.barrierplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import tw.momocraft.barrierplus.utils.PlayerPointsAPI;

import java.util.Collection;
import java.util.UUID;

public class PlayerHandler {

    private static final int PLAYER_CRAFT_INV_SIZE = 5;

    public static boolean isCraftingInv(InventoryView view) {
        return view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE;
    }

    public static boolean isCreativeMode(Player player) {
        final GameMode gamemode = player.getGameMode();
        final GameMode creative = GameMode.CREATIVE;
        return gamemode == creative;
    }

    public static boolean isAdventureMode(Player player) {
        final GameMode gamemode = player.getGameMode();
        final GameMode adventure = GameMode.ADVENTURE;
        return gamemode == adventure;
    }


    public static boolean getNewSkullMethod() {
        try {
            Class.forName("org.bukkit.inventory.meta.SkullMeta");
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

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
        } else if (priceType.equals("money") && ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy() != null) {
            return ConfigHandler.getDepends().getVault().getEconomy().getBalance(player);
        }
        return 0;
    }

    public static double takeTypeMoney(Player player, String priceType, int price) {
        if (priceType.equals("points") && ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            return playerPointsAPI.takePoints(player, price);
        } else if (priceType.equals("money") && ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy() != null) {
            ConfigHandler.getDepends().getVault().getEconomy().withdrawPlayer(player, price);
            return ConfigHandler.getDepends().getVault().getEconomy().getBalance(player);
        }
        return 0;
    }
}