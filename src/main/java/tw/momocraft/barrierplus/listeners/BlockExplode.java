package tw.momocraft.barrierplus.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.*;

public class BlockExplode implements Listener {

    /**
     *
     * @param e when an unknown block explode.
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
            ConfigurationSection blockConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List");
            if (blockConfig != null) {
                List<Material> blockList = getDestroyExplodeList(blockConfig.getKeys(false));
                List<Material> blockDropList = getDestroyExplodeDropMap(blockConfig.getKeys(false));
                Block block;
                Material blockType;
                Iterator<Block> i = e.blockList().iterator();
                while (i.hasNext()) {
                    block = i.next();
                    blockType = block.getType();
                    if (blockList.contains(blockType)) {
                        Language.debugMessage("Destroy-Explode", blockType.name(), "Explode-Break = false or null", "remove");
                        i.remove();
                    } else if (blockDropList.contains(blockType)) {
                        Language.debugMessage("Destroy-Explode-Drop", blockType.name(), "Explode-Drop = false or null", "remove", "replace to air");
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    /**
     *
     * @param explodeList the block list from explosion.
     * @return the list which will not destroys by explosion.
     */
    private List<Material> getDestroyExplodeList(Set<String> explodeList) {
        List<Material> blockList = new ArrayList<>();
        String enable;
        for (String block : explodeList) {
            enable = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Explode-Break");
            if (enable == null || enable.equals("false")) {
                blockList.add(Material.getMaterial(block));
            }
            continue;
        }
        return blockList;
    }

    /**
     *
     * @param explodeList the block list from explosion.
     * @return the list which will not drops by explosion.
     */
    private List<Material> getDestroyExplodeDropMap(Set<String> explodeList) {
        List<Material> blockList = new ArrayList<>();
        String enable;
        for (String block : explodeList) {
            enable = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Explode-Drop");
            if (enable == null || enable.equals("false")) {
                blockList.add(Material.getMaterial(block));
            }
            continue;
        }
        return blockList;
    }
}
