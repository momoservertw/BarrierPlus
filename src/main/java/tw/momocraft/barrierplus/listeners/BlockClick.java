package tw.momocraft.barrierplus.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.barrierplus.utils.SeeMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockClick implements Listener {

    private final Map<String, Long> cdSeeMap = new HashMap<>();
    private final Map<String, Long> cdDestroyMap = new HashMap<>();

    /*
     * Destroy: Hold menu & shift + left-click (the bloc)
     *
     * See: Hold menu & left-click (the bloc)
     * See: Hold same item & left-click (AIR or the Bloc)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent e) {
        boolean see = ConfigHandler.getConfigPath().isSee();
        boolean destroy = ConfigHandler.getConfigPath().isDestroy();
        if (!see && !destroy)
            return;
        Player player = e.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        String itemType = itemStack.getType().name();
        Block block = e.getClickedBlock();
        // Left click a block.
        String action = e.getAction().name();
        if (action.equals("LEFT_CLICK_BLOCK")) {
            String blockType;
            try {
                blockType = block.getType().name();
            } catch (Exception ex) {
                return;
            }
            if (player.isSneaking()) {
                // Holding menu.
                if (!CorePlusAPI.getCond().isMenu(itemStack))
                    return;
                // Destroy - Break block by menu.
                if (ConfigHandler.getConfigPath().isDestroy())
                    destroyBlock(player, block.getLocation(), blockType);
            } else {
                // See - Display near blocks.
                if (ConfigHandler.getConfigPath().isSee()) {
                    if (!CorePlusAPI.getCond().isMenu(itemStack) &&
                            !itemStack.getType().name().equals(blockType))
                        return;
                    seeBlock(player, blockType);
                }
            }
            // Left click air.
        } else if (action.equals("LEFT_CLICK_AIR")) {
            if (ConfigHandler.getConfigPath().isSee())
                seeBlock(player, itemType);
        }
    }

    private void seeBlock(Player player, String blockType) {
        SeeMap seeMap = ConfigHandler.getConfigPath().getSeeProp().get(blockType);
        if (seeMap == null)
            return;
        // Location.
        if (!CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(),
                player.getLocation(), seeMap.getLocList(), true)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Destroy", blockType, "location", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Player is on cooldown.
        if (onCooldownSee(player)) {
            if (ConfigHandler.getConfigPath().isSeeCDMsg()) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "See", blockType, "cooldown", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Creative mode disable.
        if (seeMap.getCreative().equals("false")) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                        "See", blockType, "creative", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
        }
        // Has see permission.
        if (CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see." + blockType) ||
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see.*")) {
            addCDSee(player);
            displayBlock(player, blockType, seeMap);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "See", blockType, "final", "return",
                    new Throwable().getStackTrace()[0]);
        }
    }

    private void destroyBlock(Player player, Location blockLoc, String blockType) {
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap == null)
            return;
        // Enable
        if (!destroyMap.isMenuBreak())
            return;
        // Cooldown
        if (onCooldownDestroy(player)) {
            if (ConfigHandler.getConfigPath().isDestroyCDMsg()) {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            }
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Destroy", blockType, "cooldown", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Checking the "Conditions".
        List<String> conditionList = CorePlusAPI.getMsg().transHolder(player, block, destroyMap.getConditions());
        if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(), conditionList)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginName(),
                    "Destroy", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Destroy permission
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy." + blockType)) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                    "Message.noPermission", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Destroy", blockType, "permission", "return",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Residence flag
        if (!CorePlusAPI.getCond().checkFlag(player, blockLoc, "destroy", false, true)) {
            String[] placeHolders = CorePlusAPI.getMsg().newString();
            placeHolders[13] = "destroy"; // %flag%
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(), ConfigHandler.getPrefix(),
                    "Message.noFlagPerm", player, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                    "Destroy", blockType, "residence", "return", "destroy",
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Menu Drop
        if (destroyMap.isMenuDrop()) {
            try {
                player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                        "Destroy", blockType, "Drop", "return",
                        new Throwable().getStackTrace()[0]);
            } catch (Exception ex) {
                CorePlusAPI.getMsg().sendDebugTrace(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), ex);
            }
        }
        // Destroy the block.
        addCDDestroy(player);
        blockLoc.getBlock().setType(Material.AIR);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(),
                "Destroy", blockType, "final", "return",
                new Throwable().getStackTrace()[0]);
    }

    /**
     * Display nearby creative blocks like barriers.
     *
     * @param player the trigger player.
     * @param block  the display creative blocks.
     */
    private void displayBlock(Player player, String block, SeeMap seeMap) {
        int range = ConfigHandler.getConfigPath().getSeeDistance();
        Location playerLoc = player.getLocation();
        Location loc;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    loc = playerLoc.clone().add(x, y, z);
                    if (loc.getBlock().getType().name().equals(block)) {
                        CorePlusAPI.getCmd().dispatchParticleGroup(ConfigHandler.getPrefix(), loc, seeMap.getParticle());
                    }
                }
            }
        }
    }

    private boolean onCooldownSee(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getSeeCDInterval();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (cdSeeMap.containsKey(player.getWorld().getName() + "." + player.getName()))
            playersCDList = cdSeeMap.get(player.getWorld().getName() + "." + player.getName());
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDSee(Player player) {
        cdSeeMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }

    private boolean onCooldownDestroy(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getDestroyCD();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (cdDestroyMap.containsKey(player.getWorld().getName() + "." + player.getName()))
            playersCDList = cdDestroyMap.get(player.getWorld().getName() + "." + player.getName());
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDDestroy(Player player) {
        cdDestroyMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }
}