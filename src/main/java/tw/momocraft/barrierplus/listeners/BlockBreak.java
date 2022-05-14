package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.List;

public class BlockBreak implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Player player = e.getPlayer();
        String playerName = player.getName();
        Block block = e.getBlock();
        String blockType = block.getBlockData().getMaterial().name();
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap == null)
            return;
        Location loc = block.getLocation();
        // Checking the "Conditions".
        List<String> conditionList = destroyMap.getConditions();
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, conditionList);
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, conditionList);
        if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(), conditionList)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Destroy", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Cancel vanilla break event.
        if (!destroyMap.isVanillaBreak()) {
            if (ConfigHandler.getConfigPath().isDestroyHelp())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        ConfigHandler.getConfigPath().getMsgBreakHelp(), player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "Destroy", "cancel", blockType,
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Permission.
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy." + blockType.toLowerCase()) &&
                !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*")) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    "Message.noPermission", player);
            List<String> failedCommands = destroyMap.getFailedCommands();
            failedCommands = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, failedCommands);
            failedCommands = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, failedCommands);
            CorePlusAPI.getCmd().sendCmd(ConfigHandler.getPluginName(), player, failedCommands);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "Permission", "cancel", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Residence-Flag.
        if (!CorePlusAPI.getCond().checkFlag(player, loc, "destroy", false, true)) {
            String[] placeHolders = CorePlusAPI.getMsg().newString();
            placeHolders[13] = "destroy"; // %flag%
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                    "Message.noFlagPerm", player, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "Residence-Flag", "cancel", blockType,
                    new Throwable().getStackTrace()[0]);
            e.setCancelled(true);
            return;
        }
        // Vanilla Drop
        if (destroyMap.isVanillaDrop()) {
            block.setType(Material.AIR);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "vanillaDrop", "cancel", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        List<String> commands = destroyMap.getCommands();
        commands = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, commands);
        commands = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, commands);
        CorePlusAPI.getCmd().sendCmd(ConfigHandler.getPluginName(), player, commands);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "Destroy", playerName, "none", "cancel", blockType,
                new Throwable().getStackTrace()[0]);
    }
}
