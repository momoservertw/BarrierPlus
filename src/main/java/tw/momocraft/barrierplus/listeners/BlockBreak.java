package tw.momocraft.barrierplus.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
            Player player = e.getPlayer();
            String block = e.getBlock().getBlockData().getMaterial().name();
            //Check destroy block list.
            ConfigurationSection blockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List");
            if (blockList != null) {
                if (blockList.getKeys(false).contains(block)) {
                    //Cancel vanilla break event.
                    if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.List." + block + ".Vanilla-Break")) {
                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Menu-Break.Help-Message")) {
                            Language.sendLangMessage("Message.BarrierPlus.breakHelp", player);
                        }
                        Language.debugMessage("Destroy", block , "Vanilla-Break", "cancel");
                        e.setCancelled(true);
                        return;
                    }
                    //Check destroy permission.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + block.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                        Language.debugMessage("Destroy", block , "permission", "return");
                        return;
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                    Language.debugMessage("Destroy", block , "permission", "cancel");
                    e.setCancelled(true);
                }
            }
        }
    }
}
