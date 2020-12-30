package tw.momocraft.barrierplus.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.*;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.*;

public class BlockClick implements Listener {

    private final Map<String, Long> cdSeeMap = new HashMap<>();
    private final Map<String, Long> cdDestroyMap = new HashMap<>();

    /*
     * Destroy: Hold menu & shift + left-click (the bloc)
     *
     * See: Hold menu & left-click (the bloc)
     * See: Hold same item & left-click (AIR or the Bloc)
     */
    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        boolean see = ConfigHandler.getConfigPath().isSee();
        boolean destroy = ConfigHandler.getConfigPath().isDestroy();
        if (!see && !destroy) {
            return;
        }
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
                if (!CorePlusAPI.getUtilsManager().isMenu(itemStack)) {
                    return;
                }
                // Destroy - Break block by menu.
                if (ConfigHandler.getConfigPath().isDestroy()) {
                    destroyBlock(player, block.getLocation(), blockType);
                }
            } else {
                // See - Display near blocks.
                if (ConfigHandler.getConfigPath().isSee()) {
                    if (!CorePlusAPI.getUtilsManager().isMenu(itemStack) &&
                            !itemStack.getType().name().equals(blockType)) {
                        return;
                    }
                    seeBlock(player, blockType);
                }
            }
            // Left click air.
        } else if (action.equals("LEFT_CLICK_AIR")) {
            if (ConfigHandler.getConfigPath().isSee()) {
                seeBlock(player, itemType);
            }
        }
    }

    private void seeBlock(Player player, String blockType) {
        SeeMap seeMap = ConfigHandler.getConfigPath().getSeeProp().get(blockType);
        if (seeMap != null) {
            // Location.
            if (!CorePlusAPI.getConditionManager().checkLocation(player.getLocation(), seeMap.getLocList(), true)) {
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Player is on cooldown.
            if (onCooldownSee(player)) {
                if (ConfigHandler.getConfigPath().isSeeCDMsg()) {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.cooldown", player);
                }
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "See", blockType, "cooldown", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Creative mode disable.
            if (seeMap.getCreative().equals("false")) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "See", blockType, "creative", "return",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
            }
            // Has see permission.
            if (CorePlusAPI.getPlayerManager().hasPermission(player, "barrierplus.see." + blockType) ||
                    CorePlusAPI.getPlayerManager().hasPermission(player, "barrierplus.see.*")) {
                addCDSee(player);
                displayBlock(player, blockType, seeMap);
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "See", blockType, "final", "return",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    private void destroyBlock(Player player, Location blockLoc, String blockType) {
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap != null) {
            // Enable
            if (!destroyMap.isMenuBreak()) {
                return;
            }
            // Cooldown
            if (onCooldownDestroy(player)) {
                if (ConfigHandler.getConfigPath().isDestroyCDMsg()) {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.cooldown", player);
                }
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "cooldown", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Location
            if (!CorePlusAPI.getConditionManager().checkLocation(blockLoc, destroyMap.getLocList(), true)) {
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Prevent Location
            if (CorePlusAPI.getConditionManager().checkLocation(blockLoc, destroyMap.getPreventLocList(), false)) {
                String[] placeHolders = CorePlusAPI.getLangManager().newString();
                placeHolders[9] = blockType; // %material%
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBreakLocFail(), player, placeHolders);
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "prevent location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Destroy permission
            if (!CorePlusAPI.getPlayerManager().hasPermission(player, "barrierplus.destroy." + blockType)) {
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", player);
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "permission", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence flag
            if (!CorePlusAPI.getConditionManager().checkFlag(player, blockLoc, "destroy", true, true)) {
                String[] placeHolders = CorePlusAPI.getLangManager().newString();
                placeHolders[12] = "destroy"; // %flag%
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noFlagPerm", player, placeHolders);
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "residence", "return", "destroy",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Menu Drop
            if (destroyMap.isMenuDrop()) {
                try {
                    player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "Drop", "return",
                            new Throwable().getStackTrace()[0]);
                } catch (Exception ex) {
                    CorePlusAPI.getLangManager().sendDebugTrace(ConfigHandler.getPrefix(), ex);
                }
            }
            // Destroy the block.
            addCDDestroy(player);
            blockLoc.getBlock().setType(Material.AIR);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(), "Destroy", blockType, "final", "return",
                    new Throwable().getStackTrace()[0]);
        }
    }

    /**
     * Display nearby creative blocks like barriers.
     *
     * @param player the trigger player.
     * @param block  the display creative blocks.
     */
    private void displayBlock(Player player, String block, SeeMap seeMap) {
        int range = ConfigHandler.getConfigPath().getSeeDistance();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Location loc = player.getLocation().getBlock().getLocation().clone().add(x, y, z);
                    if (loc.getBlock().getType() == Material.getMaterial(block)) {
                        CorePlusAPI.getCommandManager().executeCmd(ConfigHandler.getPrefix(), player, "Particle: " + seeMap.getParticle(), false);
                    }
                }
            }
        }
    }

    private boolean onCooldownSee(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getSeeCDInterval();
        if (cdTick == 0) {
            return false;
        }
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (cdSeeMap.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCDList = cdSeeMap.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDSee(Player player) {
        cdSeeMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }

    private boolean onCooldownDestroy(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getDestroyCD();
        if (cdTick == 0) {
            return false;
        }
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (cdDestroyMap.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCDList = cdDestroyMap.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDDestroy(Player player) {
        cdDestroyMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }
}