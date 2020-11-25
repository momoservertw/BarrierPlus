package tw.momocraft.barrierplus.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;

import java.util.*;

public class BlockDropItem implements Listener {

    /**
     * @param e when a item drops.
     */
    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent e) {
        if (ConfigHandler.getConfigPath().isDestroy()) {
            String blockType = e.getBlock().getType().name();
            DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
            String vanillaDrop = destroyMap.getVanillaDrop();
            if (vanillaDrop != null && vanillaDrop.equals("false")) {
                Item item;
                String itemType;
                Iterator<Item> i = e.getItems().iterator();
                while (i.hasNext()) {
                    item = i.next();
                    itemType = item.getItemStack().getType().name();
                    if (blockType.equals(itemType)) {
                        ServerHandler.sendFeatureMessage("Destroy", blockType, "Vanilla-Drop ", "remove",
                                new Throwable().getStackTrace()[0]);
                        i.remove();
                    }
                }
            } else if (vanillaDrop == null) {

            }
        }
    }
}
