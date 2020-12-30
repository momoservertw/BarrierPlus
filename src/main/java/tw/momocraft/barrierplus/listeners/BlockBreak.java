package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

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
        if (destroyMap == null) {
            return;
        }
        Location blockLoc = e.getBlock().getLocation();
        // Cancel vanilla break event.
        if (!destroyMap.isVanillaBreak()) {
            if (ConfigHandler.getConfigPath().isDestroyHelp()) {
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBreakHelp(), player);
            }
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "Vanilla Break", "cancel",
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Location.
        if (!CorePlusAPI.getConditionManager().checkLocation(blockLoc, destroyMap.getLocList(), true)) {
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "location", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Prevent Location.
        if (CorePlusAPI.getConditionManager().checkLocation(blockLoc, destroyMap.getPreventLocList(), false)) {
            String[] placeHolders = CorePlusAPI.getLangManager().newString();
            placeHolders[9] = blockType; // %material%
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBreakLocFail(), player, placeHolders);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "Location", "cancel",
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Has destroy permission.
        if (!CorePlusAPI.getPlayerManager().hasPermission(player, "barrierplus.destroy." + blockType.toLowerCase())) {
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", player);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "block permission", "cancel",
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Residence flag.
        if (!CorePlusAPI.getConditionManager().checkFlag(player, blockLoc, "destroy", true, true)) {
            String[] placeHolders = CorePlusAPI.getLangManager().newString();
            placeHolders[12] = "destroy"; // %flag%
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noFlagPerm", player, placeHolders);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "residence", "return",
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Vanilla Drop
        if (destroyMap.isVanillaDrop()) {
            block.setType(Material.AIR);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "vanillaDrop", "cancel",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "final", "cancel",
                new Throwable().getStackTrace()[0]);
    }
}
