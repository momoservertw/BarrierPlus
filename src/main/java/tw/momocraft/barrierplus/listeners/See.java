package tw.momocraft.barrierplus.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.SeeMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class See implements Listener {

    private final Map<String, Long> seeCDMap = new HashMap<>();
    private final Map<String, Long> destroyCDMap = new HashMap<>();

    /*
     * Destroy: Hold menu & shift + left-click (the bloc)
     *
     * See: Hold menu & left-click (the bloc)
     * See: Hold same item & left-click (AIR or the Bloc)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent e) {
        if (!ConfigHandler.getConfigPath().isSee())
            return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null)
            return;
        Action action = e.getAction();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Material material = itemStack.getType();
        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            // Menu
            if (CorePlusAPI.getCond().isMenu(itemStack)) {
                seeBlock(player, material);
                return;
            }
            if (material.equals(block.getType()))
                seeBlock(player, material);
        } else if (action.equals(Action.LEFT_CLICK_AIR)) {
            seeBlock(player, material);
        }
    }

    private void seeBlock(Player player, Material material) {
        String blockType = material.name();
        SeeMap seeMap = ConfigHandler.getConfigPath().getSeeProp().get(blockType);
        if (seeMap == null)
            return;
        // Location
        String playerName = player.getName();
        Location playerLoc = player.getLocation();
        if (!CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                playerLoc, seeMap.getLocationList(), true)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "See", playerName, "Location", "bypass", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Conditions
        if (!checkConditions(player, material, seeMap.getConditions())) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "See", playerName, "Conditions", "fail", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Creative
        if (player.getGameMode().equals(GameMode.CREATIVE))
            if (!seeMap.isCreative()) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "See", playerName, "Creative", "fail", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
        // Cooldown.
        if (onSeeCD(playerName)) {
            if (ConfigHandler.getConfigPath().isSeeCDMsg())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "See", playerName, "Cooldown", "fail", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Permission
        if (!checkPermission(blockType, player)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "See", playerName, "Permission", "fail", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        addSeeCD(playerName);
        displayBlock(player, blockType, seeMap);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "See", playerName, "Final", "success", blockType,
                new Throwable().getStackTrace()[0]);
    }

    private void displayBlock(Player player, String blockType, SeeMap seeMap) {
        int range = ConfigHandler.getConfigPath().getSeeDistance();
        Location playerLoc = player.getLocation();
        Location loc;
        for (int x = -range; x <= range; x++)
            for (int y = -range; y <= range; y++)
                for (int z = -range; z <= range; z++) {
                    loc = playerLoc.clone().add(x, y, z);
                    if (loc.getBlock().getType().name().equals(blockType))
                        CorePlusAPI.getCmd().dispatchParticleGroup(ConfigHandler.getPluginName(),
                                loc.clone().add(0.5, 0, 0.5), seeMap.getParticle());
                }
    }

    private boolean onSeeCD(String playerName) {
        int cdTick = ConfigHandler.getConfigPath().getSeeCDInterval();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (seeCDMap.containsKey(playerName))
            playersCDList = seeCDMap.get(playerName);
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addSeeCD(String playerName) {
        seeCDMap.put(playerName, System.currentTimeMillis());
    }

    private boolean checkLocation(Location blockLoc, List<String> locList) {
        if (locList == null)
            return true;
        return CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                blockLoc, locList, true);
    }

    private boolean checkConditions(Player player, Material material, List<String> conditionList) {
        if (conditionList == null)
            return true;
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), player, conditionList);
        conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), material, conditionList);
        return CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(),
                conditionList);
    }

    private boolean checkPermission(String blockType, Player player) {
        return CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see.*") ||
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see." + blockType.toLowerCase());
    }
}