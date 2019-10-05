package tw.momocraft.barrierplus.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

public class BlockBreak implements Listener {

    private boolean enableDestroyEvent = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Event");
    private static ConfigurationSection destroyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Block-List");

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Material breakBlock = e.getBlock().getBlockData().getMaterial();
        String breakBlockString = breakBlock.toString();

        if (enableDestroyEvent == true) {
            //Check destroy block list.
            if (destroyBlockList.getKeys(false).contains(breakBlockString)) {
                String seeBlockType = breakBlockString;
                //Check destroy permission.
                if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + seeBlockType.toLowerCase()) ||
                        PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                    //Display near barriers and structure_void after broke a block.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.see." + seeBlockType.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                        BlockClick.checkBarrier(player, seeBlockType);
                    }
                    //Cancel vanilla break event.
                    if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Block-List." + seeBlockType + ".Vanilla-Break")) {
                        Language.sendLangMessage("Message.Break-Help", player);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
