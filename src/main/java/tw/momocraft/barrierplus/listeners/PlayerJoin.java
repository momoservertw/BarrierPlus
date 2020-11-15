package tw.momocraft.barrierplus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onBreakBlock(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (PermissionsHandler.hasPermission(player, "barrierplus.command.version")) {
            Language.dispatchMessage(player, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
            ConfigHandler.getUpdater().checkUpdates(player);
        }
    }
}
