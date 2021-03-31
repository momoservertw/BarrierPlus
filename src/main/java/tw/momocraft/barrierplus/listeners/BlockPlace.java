package tw.momocraft.barrierplus.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.PlaceMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.List;

public class BlockPlace implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (!ConfigHandler.getConfigPath().isPlace())
            return;
        Block block = e.getBlockPlaced();
        String blockType = block.getType().name();
        PlaceMap placeMap = ConfigHandler.getConfigPath().getPlaceProp().get(blockType);
        if (placeMap == null)
            return;
        Player player = e.getPlayer();
        String playerName = player.getName();
        // Conditions
        List<String> conditionList = CorePlusAPI.getMsg().transHolder(player, block, placeMap.getConditions());
        if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPlugin(), conditionList)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Place", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Permissions
        if (CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place." + blockType) ||
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place.*")) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Place", playerName, "Permission", "bypass", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[9] = blockType;
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                ConfigHandler.getConfigPath().getMsgPlaceLocFail(), player, placeHolders);
        e.setCancelled(true);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "Place", playerName, "Final", "cancel", blockType,
                new Throwable().getStackTrace()[0]);
    }
}
