package tw.momocraft.barrierplus.listeners;

import me.RockinChaos.itemjoin.api.ItemJoinAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.*;

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
                if (!holdingMenu(itemStack, player)) {
                    return;
                }
                // Destroy - Break block by menu.
                if (ConfigHandler.getConfigPath().isDestroy()) {
                    destroyBlock(player, block.getLocation(), blockType);
                }
            } else {
                // See - Display near blocks.
                if (ConfigHandler.getConfigPath().isSee()) {
                    if (!holdingMenu(itemStack, player) && !itemStack.getType().name().equals(blockType)) {
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
            if (!ConfigHandler.getConfigPath().getLocationUtils().checkLocation(player.getLocation(), seeMap.getLocMaps(), true)) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Player is on cooldown.
            if (onCooldownSee(player)) {
                if (ConfigHandler.getConfigPath().isSeeCDMsg()) {
                    Language.sendLangMessage("Message.cooldown", player);
                }
                ServerHandler.sendFeatureMessage("See", blockType, "cooldown", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Has see permission.
            if (PermissionsHandler.hasPermission(player, "barrierplus.see." + blockType) ||
                    PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                addCDSee(player);
                displayBlock(player, blockType, seeMap);
                ServerHandler.sendFeatureMessage("See", blockType, "final", "return",
                        new Throwable().getStackTrace()[0]);
            }
            // Creative mode disable.
            if (seeMap.getCreative().equals("false")) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    ServerHandler.sendFeatureMessage("See", blockType, "creative", "return",
                            new Throwable().getStackTrace()[0]);
                }
            }
        }
    }

    private void destroyBlock(Player player, Location blockLoc, String blockType) {
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap != null) {
            // MenuBreak enable.
            if (destroyMap.getMenuBreak().equals("false")) {
                return;
            }
            // Player is on cooldown.
            if (onCooldownDestroy(player)) {
                if (ConfigHandler.getConfigPath().isDestroyCDMsg()) {
                    Language.sendLangMessage("Message.cooldown", player);
                }
                ServerHandler.sendFeatureMessage("Destroy", blockType, "cooldown", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Location.
            if (!ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getLocMaps(), true)) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Prevent Location.
            if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyMap.getPreventLocMaps(), false)) {
                String[] placeHolders = Language.newString();
                placeHolders[7] = blockType;
                Language.sendLangMessage("Message.BarrierPlus.breakLocFail", player, placeHolders);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "prevent location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Has destroy permission.
            if (!PermissionsHandler.hasPermission(player, "barrierplus.destroy." + blockType) &&
                    !PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                Language.sendLangMessage("Message.BarrierPlus.noPermBreak", player);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "permission", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Residence flag.
            if (!ResidenceUtils.checkFlag(player, blockLoc, true, "destroy")) {
                Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "residence", "return", "destroy",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Menu Drop.
            if (destroyMap.getMenuDrop().equals("true")) {
                try {
                    player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                    ServerHandler.sendFeatureMessage("Destroy", blockType, "Drop", "return",
                            new Throwable().getStackTrace()[0]);
                } catch (Exception ex) {
                    ServerHandler.sendDebugTrace(ex);
                }
            }
            // Destroy the block.
            addCDDestroy(player);
            blockLoc.getBlock().setType(Material.AIR);
            ServerHandler.sendFeatureMessage("Destroy", blockType, "final", "return",
                    new Throwable().getStackTrace()[0]);
        }
    }

    private boolean holdingMenu(ItemStack itemStack, Player player) {
        // Holding ItemJoin menu.
        if (ConfigHandler.getDepends().ItemJoinEnabled()) {
            ItemJoinAPI itemJoinAPI = new ItemJoinAPI();
            String menuIJ = ConfigHandler.getConfigPath().getMenuIJ();
            if (!menuIJ.equals("")) {
                if (itemJoinAPI.getNode(itemStack) != null) {
                    return itemJoinAPI.getNode(itemStack).equals(menuIJ);
                }
                return false;
            }
        }
        // Holding a menu item.
        if (itemStack.getType().name().equals(ConfigHandler.getConfigPath().getMenuType())) {
            String itemName;
            try {
                itemName = itemStack.getItemMeta().getDisplayName();
            } catch (Exception ex) {
                itemName = "";
            }
            String menuName = ConfigHandler.getConfigPath().getMenuName();
            return menuName.equals("") || itemName.equals(Utils.translateColorCode(menuName));
        }
        return false;
    }

    /**
     * Display nearby creative blocks like barriers.
     *
     * @param player the trigger player.
     * @param block  the display creative blocks.
     */
    private void displayBlock(Player player, String block, SeeMap seeMap) {
        List<Location> locations = new ArrayList<>();
        int range = ConfigHandler.getConfigPath().getSeeDistance();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Location loc = player.getLocation().getBlock().getLocation().clone().add(x, y, z);
                    if (loc.getBlock().getType() == Material.getMaterial(block)) {
                        locations.add(loc.add(0.5D, 0.5D, 0.5D));
                    }
                }
            }
        }
        Particle particle = Particle.valueOf(seeMap.getParticle());
        int amount = seeMap.getAmount();
        int times = seeMap.getTimes();
        int interval = seeMap.getInterval();
        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                if (i > times) {
                    ServerHandler.sendFeatureMessage("See", block, "particle", "cancel",
                            new Throwable().getStackTrace()[0]);
                    cancel();
                } else {
                    ++i;
                    locations.forEach((loc) -> player.spawnParticle(particle, loc, amount, 0, 0, 0, 0));
                    ServerHandler.sendFeatureMessage("See", block, "particle", "continue",
                            new Throwable().getStackTrace()[0]);
                }
            }
        }.runTaskTimer(BarrierPlus.getInstance(), 0, interval);
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