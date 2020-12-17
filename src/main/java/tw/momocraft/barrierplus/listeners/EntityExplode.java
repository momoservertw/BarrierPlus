package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.*;

public class EntityExplode implements Listener {

    /**
     * @param e when an entity explode like Creeper, Wither, TNT.
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy()) {
            return;
        }
        Block block;
        String blockType;
        Location blockLoc;
        Iterator<Block> i = e.blockList().iterator();
        while (i.hasNext()) {
            block = i.next();
            blockLoc = block.getLocation();
            blockType = block.getType().name();
            DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
            if (destroyMap == null) {
                continue;
            }
            // Location
            if (!CorePlusAPI.getLocationManager().checkLocation(blockLoc, destroyMap.getLocMaps(), true)) {
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "location", "continue", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Prevent Location
            if (CorePlusAPI.getLocationManager().checkLocation(blockLoc, destroyMap.getPreventLocMaps(), true)) {
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "prevent location", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
                i.remove();
                continue;
            }
            // Residence flag
            if (!CorePlusAPI.getResidenceManager().checkFlag(null, blockLoc, true, "destroy")) {
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "residence", "continue", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode break
            if (!destroyMap.isExplodeBreak()) {
                i.remove();
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "destroy", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode drop
            if (!destroyMap.isExplodeDrop()) {
                block.setType(Material.AIR);
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "drop", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }
}
