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
        if (ConfigHandler.getConfigPath().isPlace()) {
            Block block = e.getBlockPlaced();
            String blockType = block.getBlockData().getMaterial().name();
            List<LocationMap> placeMap = ConfigHandler.getConfigPath().getPlaceProp().get(block);
            if (placeMap == null) {
                return;
            }
            Location loc = block.getLocation();
            if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(loc, placeMap)) {
                Player player = e.getPlayer();
                // Has bypass permission.
                if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.place")) {
                    ServerHandler.sendFeatureMessage("Place", blockType, " bypasspermission", "bypass",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                //Check placing permissions.
                if (PermissionsHandler.hasPermission(player, "barrierplus.place." + blockType) ||
                        PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                    String[] placeHolders = Language.newString();
                    placeHolders[7] = blockType;
                    Language.sendLangMessage("Message.BarrierPlus.placeLocFail", player, placeHolders);
                    ServerHandler.sendFeatureMessage("Place", blockType, "permmission", "fail",
                            new Throwable().getStackTrace()[0]);
                    e.setCancelled(true);
                }
            }
        }
    }
}
