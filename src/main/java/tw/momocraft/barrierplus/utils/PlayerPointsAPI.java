package tw.momocraft.barrierplus.utils;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerPointsAPI {

    private PlayerPoints pp;

    public PlayerPointsAPI() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (plugin != null) {
            pp = (PlayerPoints.class.cast(plugin));
        }
    }

    public double getPoints(Player player) {
        return pp.getAPI().look(player.getUniqueId());
    }

    public double setPoints(Player player, double points) {
        pp.getAPI().set(player.getUniqueId(), (int) points);
        return points;
    }

    public double takePoints(Player player, double points) {
        pp.getAPI().take(player.getUniqueId(), (int) points);
        return getPoints(player);
    }

    public double givePoints(Player player, double points) {
        pp.getAPI().give(player.getUniqueId(), (int) points);
        return getPoints(player);
    }

    public boolean usesDoubleValues() {
        return false;
    }

}
