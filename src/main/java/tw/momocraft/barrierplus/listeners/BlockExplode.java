package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.Iterator;
import java.util.List;

public class BlockExplode implements Listener {

    /**
     * @param e when an unknown block explode.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Block block;
        String blockType;
        Location blockLoc;
        Iterator<Block> iterator = e.blockList().iterator();
        while (iterator.hasNext()) {
            block = iterator.next();
            blockLoc = block.getLocation();
            blockType = block.getType().name();
            DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
            if (destroyMap == null)
                continue;
            // Conditions
            List<String> conditionList = CorePlusAPI.getMsg().transHolder(null, block, destroyMap.getConditions());
            if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPlugin(), conditionList)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                        "Destroy-Explode", blockType, "Condition", "none",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence-Flag
            if (!CorePlusAPI.getCond().checkFlag(null, blockLoc, "destroy", false, true)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Residence-Flag", "bypass",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode Break
            if (!destroyMap.isExplodeBreak()) {
                iterator.remove();
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Destroy", "bypass",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode Drop
            if (!destroyMap.isExplodeDrop()) {
                block.setType(Material.AIR);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Drop", "bypass",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }
}
