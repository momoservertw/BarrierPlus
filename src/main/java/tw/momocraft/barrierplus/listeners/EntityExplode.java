package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.barrierplus.utils.ResidenceUtils;

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
            // Location.
            if (!ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getLocMaps(), true)) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "location", "continue", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Prevent Location.
            if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getPreventLocMaps(), false)) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "prevent location", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
                i.remove();
                continue;
            }
            // Residence flag.
            if (!ResidenceUtils.checkFlag(null, blockLoc, true, "destroy")) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "residence", "continue", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode break.
            String explodeBreak = destroyMap.getExplodeBreak();
            if (explodeBreak != null && explodeBreak.equals("false")) {
                i.remove();
                ServerHandler.sendFeatureMessage("Destroy", blockType, "destroy", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode drop.
            String explodeDrop = destroyMap.getExplodeDrop();
            if (explodeDrop != null && explodeDrop.equals("false")) {
                block.setType(Material.AIR);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "drop", "bypass", "Explode",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }
}
