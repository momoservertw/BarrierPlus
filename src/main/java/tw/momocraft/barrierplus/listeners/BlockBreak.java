package tw.momocraft.barrierplus.listeners;

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

    private boolean enableDestroyEvent = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
    private static ConfigurationSection destroyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Block-List");

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {

        if (enableDestroyEvent == true) {
            Player player = e.getPlayer();
            Material breakBlock = e.getBlock().getBlockData().getMaterial();
            String breakBlockString = breakBlock.toString();
            //Check destroy block list.
            if (destroyBlockList.getKeys(false).contains(breakBlockString)) {
                String destroyBlockType = breakBlockString;
                //Cancel vanilla break event.
                if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Block-List." + destroyBlockType + ".Vanilla-Break")) {
                    if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Break-Help-Message")) {
                    Language.sendLangMessage("Message.Break-Help", player);
                    }
                    e.setCancelled(true);
                    return;
                }
                //Check destroy permission.
                if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + destroyBlockType.toLowerCase()) ||
                        PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                    return;
                }
                Language.sendLangMessage("Message.No-Perm-Destroy", player);
                e.setCancelled(true);
                return;
            }
        }
    }
}
