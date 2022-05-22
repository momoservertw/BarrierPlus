package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Destroy implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Block block = e.getBlock();
        String blockType = block.getBlockData().getMaterial().name();
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap == null)
            return;
        Player player = e.getPlayer();
        String playerName = player.getName();
        // Location
        Location blockLoc = block.getLocation();
        if (!checkLocation(blockLoc, destroyMap.getLocationList())) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Destroy", blockType, "Location", "bypass",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Conditions
        if (!checkConditions(player, block, destroyMap.getConditions())) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Destroy", blockType, "Condition", "bypass",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Bypass
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*") &&
                !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*")) {
            // Vanilla break
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
            // Residence
            if (!checkResidence(player, block.getLocation())) {
                String[] placeHolders = CorePlusAPI.getMsg().newString();
                placeHolders[13] = "destroy"; // %flag%
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.noFlagPerm", player, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy", blockType, "Residence", "cancel",
                        new Throwable().getStackTrace()[0]);
                e.setCancelled(true);
                return;
            }
            // Permission
            if (!checkPermission(blockType, player)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.noPermission", player);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy", playerName, "Permission", "cancel", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        // Vanilla Drop
        if (destroyMap.isVanillaDrop()) {
            block.setType(Material.AIR);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "vanillaDrop", "cancel", blockType,
                    new Throwable().getStackTrace()[0]);
        }
    }

    /**
     * @param e when an entity explode like Creeper, Wither, TNT.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Block block;
        String blockType;
        Location blockLoc;
        Iterator<Block> iterator = e.blockList().iterator();
        while (iterator.hasNext()) {
            block = iterator.next();
            blockType = block.getType().name();
            DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
            if (destroyMap == null)
                continue;
            // Location
            blockLoc = block.getLocation();
            if (!checkLocation(blockLoc, destroyMap.getLocationList())) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy-Explode", blockType, "Location", "bypass",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Conditions
            if (!checkConditions(null, block, destroyMap.getConditions())) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy-Explode", blockType, "Condition", "bypass",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence
            if (!CorePlusAPI.getCond().checkFlag(blockLoc, "destroy", false, true)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Residence-Flag", "bypass",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode Break
            if (!destroyMap.isExplodeBreak()) {
                iterator.remove();
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Destroy", "bypass",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode Drop
            if (!destroyMap.isExplodeDrop()) {
                block.setType(Material.AIR);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Drop", "bypass",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    /**
     * @param e when an unknown block explode.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Block block;
        String blockType;
        List<String> conditionList;
        DestroyMap destroyMap;
        Iterator<Block> iterator = e.blockList().iterator();
        Location blockLoc;
        while (iterator.hasNext()) {
            block = iterator.next();
            blockType = block.getType().name();
            destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
            if (destroyMap == null)
                continue;
            // Location
            blockLoc = block.getLocation();
            if (!checkLocation(blockLoc, destroyMap.getLocationList())) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy-Explode", blockType, "Location", "bypass",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Conditions
            conditionList = destroyMap.getConditions();
            if (conditionList != null) {
                conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, conditionList);
                if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(),
                        conditionList)) {
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Destroy-Explode", blockType, "Conditions", "bypass",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
            }
            // Explode Break
            if (!destroyMap.isExplodeBreak()) {
                iterator.remove();
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Destroy", "remove",
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            // Explode Drop
            if (!destroyMap.isExplodeDrop()) {
                block.setType(Material.AIR);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy-Explode", blockType, "Drop", "remove",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    private final Map<String, Long> menuDestroyCDMap = new HashMap<>();

    /*
     * Destroy: Hold menu & shift + left-click (the bloc)
     *
     * See: Hold menu & left-click (the bloc)
     * See: Hold same item & left-click (AIR or the Bloc)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent e) {
        if (!ConfigHandler.getConfigPath().isDestroy())
            return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null)
            return;
        if (e.getAction().name().equals("LEFT_CLICK_BLOCK")) {
            if (player.isSneaking()) {
                // Holding menu.
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (!CorePlusAPI.getCond().isMenu(itemStack))
                    return;
                destroyBlock(player, block);
            }
        }
    }

    private void destroyBlock(Player player, Block block) {
        String blockType = block.getType().name();
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap == null)
            return;
        if (!destroyMap.isMenuBreak())
            return;
        String playerName = player.getName();
        // Location
        Location blockLoc = block.getLocation();
        if (!CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                blockLoc, destroyMap.getLocationList(), true)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                    "Destroy-Explode", blockType, "Location", "bypass",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Conditions
        List<String> conditionList = destroyMap.getConditions();
        if (conditionList != null) {
            conditionList = CorePlusAPI.getMsg().transHolder(ConfigHandler.getPluginName(), block, conditionList);
            if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(),
                    conditionList)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy", blockType, "Conditions", "bypass",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        // Cooldown
        if (onMenuDestroyCD(playerName)) {
            if (ConfigHandler.getConfigPath().isDestroyCDMsg())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "cooldown", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Bypass
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*") &&
                !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*")) {
            // Destroy permission
            if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy." + blockType)) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.noPermission", player);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy", playerName, "permission", "cancel", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence
            if (!CorePlusAPI.getCond().checkFlag(player, blockLoc, "destroy", false, true)) {
                String[] placeHolders = CorePlusAPI.getMsg().newString();
                placeHolders[13] = "destroy"; // %flag%
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                        "Message.noFlagPerm", player, placeHolders);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy", playerName, "Residence-Flag: destroy", "none", blockType,
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        // Menu Drop
        if (destroyMap.isMenuDrop()) {
            try {
                player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy", playerName, "Drop", "succeed", blockType,
                        new Throwable().getStackTrace()[0]);
            } catch (Exception ex) {
                CorePlusAPI.getMsg().sendDebugTrace(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(), ex);
            }
        }
        // Destroy the block.
        addMenuDestroyCD(player);
        blockLoc.getBlock().setType(Material.AIR);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "Destroy", playerName, "Final", "succeed", blockType,
                new Throwable().getStackTrace()[0]);
    }

    private boolean onMenuDestroyCD(String playerName) {
        int cdTick = ConfigHandler.getConfigPath().getDestroyCD();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (menuDestroyCDMap.containsKey(playerName))
            playersCDList = menuDestroyCDMap.get(playerName);
        return (System.currentTimeMillis() - playersCDList) < cdMillis;
    }

    private void addMenuDestroyCD(Player player) {
        menuDestroyCDMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
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
        return !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy." + blockType.toLowerCase()) &&
                !CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy.*");
    }
}
