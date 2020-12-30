package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.List;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (!ConfigHandler.getConfigPath().isPlace()) {
            return;
        }
        Block block = e.getBlockPlaced();
        String blockType = block.getType().name();
        List<String> preventLocList = ConfigHandler.getConfigPath().getPlaceProp().get(blockType);
        Player player = e.getPlayer();
        // Prevent Location
        Location loc = block.getLocation();
        if (CorePlusAPI.getConditionManager().checkLocation(loc, preventLocList, true)) {
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Place", blockType, "Location", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Permissions
        if (CorePlusAPI.getPlayerManager().hasPermission(player, "barrierplus.place." + blockType)) {
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Place", blockType, "permission", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        String[] placeHolders = CorePlusAPI.getLangManager().newString();
        placeHolders[9] = blockType;
        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgPlaceLocFail(), player, placeHolders);
        e.setCancelled(true);
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Place", blockType, "permission", "fail",
                new Throwable().getStackTrace()[0]);
    }
}
