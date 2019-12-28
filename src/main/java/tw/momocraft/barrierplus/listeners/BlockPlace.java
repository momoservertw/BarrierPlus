package tw.momocraft.barrierplus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable")) {
            Player player = e.getPlayer();
            String block = e.getBlockPlaced().getBlockData().getMaterial().name();

            if (ConfigHandler.getConfig("config.yml").getStringList("Place.List").contains(block)) {
                //Check placing permissions.
                if (PermissionsHandler.hasPermission(player, "barrierplus.place." + block.toLowerCase()) ||
                PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                    ServerHandler.debugMessage("(BlockPlace) Place", block, "permission", "bypass");
                    return;
                }
                Language.sendLangMessage("Message.BarrierPlus.noPermPlace", player);
                ServerHandler.debugMessage("(BlockPlace) Place", block, "permission", "cancel");
                e.setCancelled(true);
            }
        }
    }
}
