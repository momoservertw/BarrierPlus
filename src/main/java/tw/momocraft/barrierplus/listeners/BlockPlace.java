package tw.momocraft.barrierplus.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (e.getBlock().getType() == Material.BARRIER) {
            //放置屏障的權限 | Check permission.
            if (!PermissionsHandler.hasPermission(player, "barrierplus.barrier.place")) {
                Language.sendLangMessage("Message.No-Perm-Barrier-Place", player);
                e.setCancelled(true);
            }
        }
    }
}
