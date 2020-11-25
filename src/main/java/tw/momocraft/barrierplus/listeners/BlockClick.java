package tw.momocraft.barrierplus.listeners;

import me.RockinChaos.itemjoin.api.ItemJoinAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.barrierplus.utils.Language;
import tw.momocraft.barrierplus.utils.ResidenceUtils;
import tw.momocraft.barrierplus.utils.SeeMap;

import java.util.*;

public class BlockClick implements Listener {

    private Map<String, Long> cdSeeList = new HashMap<>();
    private Map<String, Long> cdDestroyList = new HashMap<>();

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        // Holding a menu item.
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemJoinAPI itemJoinAPI = new ItemJoinAPI();
        if (!ConfigHandler.getConfigPath().getMenuIJ().equals("")) {
            if (!itemJoinAPI.isCustom(itemStack)) {
                return;
            }
        }
        Block block = e.getClickedBlock();
        String blockType = block.getBlockData().getMaterial().name();
        Location blockLoc = e.getClickedBlock().getLocation();
        if (itemStack.getType().name().equals(ConfigHandler.getConfigPath().getMenuType())) {
            String menuName = ConfigHandler.getConfigPath().getMenuName();
            if (!menuName.equals("") && !itemStack.getItemMeta().getDisplayName().equals(menuName.replace("&", "§"))) {
                if (itemStack.getType().name().equals(blockType)) {
                    seeBlock(player, blockType);
                    return;
                }
            }
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                // See - Display near blocks.
                if (!ConfigHandler.getConfigPath().isSee()) {
                    // Left click a block.
                    seeBlock(player, blockType);
                }
            } else {
                // Destroy - Break block by menu.
                if (ConfigHandler.getConfigPath().isDestroy()) {
                    if (e.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking()) {
                        destroyBlock(player, blockLoc, blockType);
                    }
                }
            }
        }
    }

    private void seeBlock(Player player, String blockType) {
        SeeMap seeMap = ConfigHandler.getConfigPath().getSeeProp().get(blockType);
        if (seeMap != null) {
            String creature = seeMap.getCreative();
            if (creature != null && creature.equals("false")) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    ServerHandler.sendFeatureMessage("See", blockType, "creative", "return",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
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
                checkBlock(player, blockType, seeMap);
                ServerHandler.sendFeatureMessage("See", blockType, "final", "return",
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    private void destroyBlock(Player player, Location blockLoc, String blockType) {
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        DestroyMap destroyDefMap = ConfigHandler.getConfigPath().getDestroyProp().get("default");
        if (destroyMap != null) {
            // MenuBreak enable.
            if (destroyMap.getMenuBreak() == null && !Boolean.parseBoolean(destroyDefMap.getMenuBreak()) ||
                    destroyMap.getMenuBreak() != null && destroyMap.getMenuBreak().equals("false")) {
                return;
            }
            // Location.
            if (!ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyDefMap.getLocMaps())) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Has destroy permission.
            if (!PermissionsHandler.hasPermission(player, "barrierplus.destroy." + blockType) &&
                    !PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "permission", "return",
                        new Throwable().getStackTrace()[0]);
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
            // Residence - Enable.
            if (ConfigHandler.getDepends().ResidenceEnabled()) {
                if (!ResidenceUtils.getBuildPerms(blockLoc, "destroy", false, player)) {
                    Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                    ServerHandler.sendFeatureMessage("Destroy", blockType, "residence", "return", "destroy",
                            new Throwable().getStackTrace()[0]);
                    return;
                }
            }
            // Prevent Location.
            if (ConfigHandler.getConfigPath().getLocationUtils().checkLocation(blockLoc, destroyDefMap.getPreventLocMaps())) {
                String[] placeHolders = Language.newString();
                placeHolders[7] = blockType;
                Language.sendLangMessage("Message.BarrierPlus.breakLocFail", player, placeHolders);
                ServerHandler.sendFeatureMessage("Destroy", blockType, "prevent location", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // Has destroy bypass permission.
            if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.destroy")) {
                ServerHandler.sendFeatureMessage("Destroy", blockType, "bypass permission", "bypass",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            // MenuDrop enable.
            if (destroyMap.getMenuDrop() == null && Boolean.parseBoolean(destroyDefMap.getMenuDrop()) ||
                    destroyMap.getMenuDrop() != null && destroyMap.getMenuDrop().equals("true")) {
                player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                ServerHandler.sendFeatureMessage("Destroy", blockType, "Drop", "return",
                        new Throwable().getStackTrace()[0]);
                return;
            }
            addCDDestroy(player);
            blockLoc.getBlock().setType(Material.AIR);
            ServerHandler.sendFeatureMessage("Destroy", blockType, "final", "return",
                    new Throwable().getStackTrace()[0]);
        }
    }

    /**
     * Display nearby creative blocks like barriers.
     *
     * @param player the trigger player.
     * @param block  the display creative blocks.
     */
    private void checkBlock(Player player, String block, SeeMap seeMap) {
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
        if (cdSeeList.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCDList = cdSeeList.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDSee(Player player) {
        cdSeeList.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }

    private boolean onCooldownDestroy(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getDestroyCD();
        if (cdTick == 0) {
            return false;
        }
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (cdDestroyList.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCDList = cdDestroyList.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addCDDestroy(Player player) {
        cdDestroyList.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }
}