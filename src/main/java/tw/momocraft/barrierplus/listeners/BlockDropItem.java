package tw.momocraft.barrierplus.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.*;

public class BlockDropItem implements Listener {

    /**
     *
     * @param e when a item drops.
     */
    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
            ConfigurationSection blockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List");
            if (blockList != null) {
                List<Material> dropList = getDestroyDropList(blockList.getKeys(false));
                Item item;
                Material itemType;
                Iterator<Item> i = e.getItems().iterator();
                while (i.hasNext()) {
                    item = i.next();
                    itemType = item.getItemStack().getType();
                    if (dropList.contains(itemType)) {
                        ServerHandler.debugMessage("(BlockDropItem) Destroy", itemType.name(), "Vanilla-Drop = false or null", "remove");
                        i.remove();
                    }
                }
            }
        }
    }

    /**
     *
     * @param dropList the list of drop items.
     * @return the list which will not drops.
     */
    private List<Material> getDestroyDropList(Set<String> dropList) {
        List<Material> blockMap = new ArrayList<>();
        String enable;
        for (String block : dropList) {
            enable = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Vanilla-Drop");
            if (enable == null || enable.equals("false")) {
                blockMap.add(Material.getMaterial(block));
            }
        }
        return blockMap;
    }
}
