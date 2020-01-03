package tw.momocraft.barrierplus.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;

import java.util.*;

public class Utils {

    public static String stripLogColors(CommandSender sender, String message) {
        if (sender instanceof ConsoleCommandSender && ConfigHandler.getConfig("config.yml").getBoolean("Log-Coloration") != true) {
            return ChatColor.stripColor(message);
        }
        return message;
    }

    public static int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    public static String getNearbyPlayer(Player player, int range) {
        ArrayList<Location> sight = new ArrayList<Location>();
        ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        Location origin = player.getEyeLocation();
        sight.add(origin.clone().add(origin.getDirection()));
        sight.add(origin.clone().add(origin.getDirection().multiply(range)));
        sight.add(origin.clone().add(origin.getDirection().multiply(range + 3)));
        for (int i = 0; i < sight.size(); i++) {
            for (int k = 0; k < entities.size(); k++) {
                if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
                    if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
                        if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
                            if (entities.get(k) instanceof Player) {
                                return ((Player) entities.get(k)).getName();
                            }
                        }
                    }
                }
            }
        }
        return "INVALID";
    }

    public static String translateLayout(String name, Player player, String... placeHolder) {
        String playerName = "EXEMPT";

        if (player != null) {
            playerName = player.getName();
        }

        if (player != null && !(player instanceof ConsoleCommandSender)) {
            try {
                name = name.replace("%player%", playerName);
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS)));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS)));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_food%", String.valueOf(player.getFoodLevel()));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_health%", String.valueOf(player.getHealth()));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "");
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
            try {
                name = name.replace("%player_interact%", getNearbyPlayer(player, 3));
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }
        if (player == null) {
            try {
                name = name.replace("%player%", "CONSOLE");
            } catch (Exception e) {
                ServerHandler.sendDebugTrace(e);
            }
        }

        name = ChatColor.translateAlternateColorCodes('&', name).toString();
        if (ConfigHandler.getDepends().PlaceHolderAPIEnabled()) {
            try {
                try {
                    return PlaceholderAPI.setPlaceholders(player, name);
                } catch (NoSuchFieldError e) {
                    ServerHandler.sendDebugMessage("Error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI.");
                    return name;
                }
            } catch (Exception e) {
            }
        }
        return name;
    }
}