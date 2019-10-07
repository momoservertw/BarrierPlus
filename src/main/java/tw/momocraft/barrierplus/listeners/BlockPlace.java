package tw.momocraft.barrierplus.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.List;

public class BlockPlace implements Listener {

    private static boolean enablePlaceEvent = ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable");
    private static List<String> placeBlockList = ConfigHandler.getConfig("config.yml").getStringList("Place.Block-List");

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (enablePlaceEvent == true) {
            Player player = e.getPlayer();
            Material placeBlock = e.getBlockPlaced().getBlockData().getMaterial();
            String placeBlockString = placeBlock.toString();

            if (placeBlockList.contains(placeBlockString)) {
                //Check placing permissions.
                if (PermissionsHandler.hasPermission(player, "barrierplus.place." + placeBlockString.toLowerCase()) ||
                PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                    return;
                }
                Language.sendLangMessage("Message.No-Perm-Place", player);
                e.setCancelled(true);
            }
        }
    }
}
