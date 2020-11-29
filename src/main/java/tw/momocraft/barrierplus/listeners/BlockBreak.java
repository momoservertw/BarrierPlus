package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.barrierplus.utils.Language;
import tw.momocraft.barrierplus.utils.ResidenceUtils;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy()) {
            return;
        }
        Player player = e.getPlayer();
        Block block = e.getBlock();
        String blockType = block.getBlockData().getMaterial().name();
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap != null) {
            Location blockLoc = e.getBlock().getLocation();
            // Cancel vanilla break event.
            if (destroyMap.getVanillaBreak().equals("false")) {
                if (ConfigHandler.getConfigPath().isDestroyHelp()) {
                    Language.sendLangMessage("Message.BarrierPlus.breakHelp", player);
                }
                ServerHandler.sendFeatureMessage("Destroy", blockType, "getVanillaBreak", "cancel",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
                return;
            }
            // Location.
            if (!ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getLocMaps(), true)) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Prevent Location.
            if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getPreventLocMaps(), false)) {
                String[] placeHolders = Language.newString();
                placeHolders[7] = blockType;
                Language.sendLangMessage("Message.BarrierPlus.breakLocFail", player, placeHolders);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "prevent location", "return",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
                return;
            }
            // Has destroy permission.
            if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + blockType.toLowerCase()) ||
                    PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "block permission", "cancel",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
                return;
            }
            // Residence flag.
            if (!ResidenceUtils.checkFlag(player, blockLoc, true, "destroy")) {
                Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "residence", "return",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
                return;
            }
            // Vanilla Drop
            String vanillaDrop = destroyMap.getVanillaDrop();
            if (vanillaDrop != null && vanillaDrop.equals("false")) {
                block.setType(Material.AIR);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "vanillaDrop", "cancel",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            ServerHandler.sendFeatureMessage("Destroy", blockType, "final", "cancel",
                    new Throwable().getStackTrace()[0]);
        }
    }
}
