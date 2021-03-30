package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.List;

public class BlockPlace implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (!ConfigHandler.getConfigPath().isPlace())
            return;
        Block block = e.getBlockPlaced();
        String blockType = block.getType().name();
        List<String> preventLocList = ConfigHandler.getConfigPath().getPlaceProp().get(blockType);
        Player player = e.getPlayer();
        // Prevent Location
        Location loc = block.getLocation();
        if (CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(), loc, preventLocList, true)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Place", blockType, "Location", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Permissions
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place." + blockType) &&
                !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place.*")) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Place", blockType, "permission", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[9] = blockType;
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                ConfigHandler.getConfigPath().getMsgPlaceLocFail(), player, placeHolders);
        e.setCancelled(true);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                "Place", blockType, "permission", "fail",
                new Throwable().getStackTrace()[0]);
    }
}
