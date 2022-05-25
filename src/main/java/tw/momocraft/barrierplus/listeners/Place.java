package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
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

public class Place implements Listener {

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
        // Bypass
        if (CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.bypass.*") &&
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.bypass.*")) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Place", playerName, "Bypass Permission", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Location
        Location blockLoc = block.getLocation();
        if (!checkLocation(blockLoc, placeMap.getLocationList())) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Place", playerName, "Location", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Conditions
        if (!checkConditions(player, block, placeMap.getConditions())) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Place", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
            // Permissions
            if (!checkPermission(blockType, player)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Place", playerName, "Permission", "cancel", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence
            if (!checkResidence(player, blockLoc)) {
                String[] placeHolders = CorePlusAPI.getMsg().newString();
                placeHolders[13] = "place"; // %flag%
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.noFlagPerm", player, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Place", playerName, "Residence-Flag: place", "none", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
        String[] placeHolders = CorePlusAPI.getMsg().newString();
        placeHolders[9] = blockType;
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                ConfigHandler.getConfigPath().getMsgPlaceLocFail(), player, placeHolders);
        e.setCancelled(true);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "Place", playerName, "Final", "cancel", blockType,
                new Throwable().getStackTrace()[0]);
    }

    private boolean checkLocation(Location blockLoc, List<String> locList) {
        if (locList == null)
            return true;
        return CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                blockLoc, locList, true);
    }

    private boolean checkConditions(Player player, Block block, List<String> conditionList) {
        if (conditionList == null)
            return true;
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, conditionList);
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, conditionList);
        return CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(),
                conditionList);
    }

    private boolean checkResidence(Player player, Location blockLoc) {
        return CorePlusAPI.getCond().checkFlag(player, blockLoc, "destroy", false, true);
    }

    private boolean checkPermission(String blockType, Player player) {
        return CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place.*") ||
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.place.*" + blockType.toLowerCase());
    }
}
