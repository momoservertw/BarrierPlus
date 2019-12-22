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
        if (gamemode == creative) {
            return true;
        }
        return false;
    }

    public static boolean isAdventureMode(Player player) {
        final GameMode gamemode = player.getGameMode();
        final GameMode adventure = GameMode.ADVENTURE;
        if (gamemode == adventure) {
            return true;
        }
        return false;
    }


    public static boolean getNewSkullMethod() {
        try {
            if (Class.forName("org.bukkit.inventory.meta.SkullMeta").getMethod("getOwningPlayer") != null) {
                return true;
            }
        } catch (Exception e) {
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

    public static String getPlayerID(Player player) {
        if (player != null && player.getUniqueId() != null) {
            return player.getUniqueId().toString();
        } else if (player != null) {
            return player.getName();
        }
        return "";
    }

    public static String getOfflinePlayerID(OfflinePlayer player) {
        if (player != null && player.getUniqueId() != null) {
            return player.getUniqueId().toString();
        } else if (player != null) {
            return player.getName();
        }
        return "";
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        Collection<?> playersOnlineNew;
        OfflinePlayer[] playersOnlineOld;
        try {
            if (Bukkit.class.getMethod("getOfflinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
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

    public static boolean getEnoughMoney(Player player, String priceType, int price) {
        double balance = getTypeBalance(player, priceType);
        return balance >= price;
    }

    public static double getTypeBalance(Player player, String priceType) {
        if (priceType.equals("points") && ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            return playerPointsAPI.getPoints(player);
        } else if (priceType.equals("money") && ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy() != null) {
            return ConfigHandler.getDepends().getVault().getBalance(player);
        }
        return 0;
    }

    public static double takeTypeMoney(Player player, String priceType, int price) {
        if (priceType.equals("points") && ConfigHandler.getDepends().PlayerPointsEnabled()) {
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI();
            return playerPointsAPI.takePoints(player, price);
        } else if (priceType.equals("money") && ConfigHandler.getDepends().getVault().vaultEnabled() && ConfigHandler.getDepends().getVault().getEconomy() != null) {
            ConfigHandler.getDepends().getVault().withdrawBalance(player, price);
            return ConfigHandler.getDepends().getVault().getBalance(player);
        }
        return 0;
    }

}