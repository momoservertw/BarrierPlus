package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;
import tw.momocraft.barrierplus.utils.locationutils.LocationMap;

import java.util.List;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (!ConfigHandler.getConfigPath().isPlace()) {
            return;
        }
        Block block = e.getBlockPlaced();
        String blockType = block.getType().name();
        List<LocationMap> preventLocMaps = ConfigHandler.getConfigPath().getPlaceProp().get(blockType);
        Player player = e.getPlayer();
        Location loc = block.getLocation();
        if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(loc, preventLocMaps, true)) {
            //Check placing permissions.
            if (!PermissionsHandler.hasPermission(player, "barrierplus.place." + blockType) &&
                    !PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                String[] placeHolders = Language.newString();
                placeHolders[7] = blockType;
                Language.sendLangMessage("Message.BarrierPlus.placeLocFail", player, placeHolders);
                ServerHandler.sendFeatureMessage("Place", blockType, "permission", "fail",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
            }
        }
    }
}
