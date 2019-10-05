package tw.momocraft.barrierplus.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.*;

public class BlockClick implements Listener {

    private static String menuItemType = ConfigHandler.getConfig("config.yml").getString("Menu.Item-Type");
    private static String menuItemName = ConfigHandler.getConfig("config.yml").getString("Menu.Item-Name").replace("&", "§");
    private static boolean enableSeeEvent = ConfigHandler.getConfig("config.yml").getBoolean("See.Enable");
    private static int range = ConfigHandler.getConfig("config.yml").getInt("See.Distance");
    private static ConfigurationSection seeBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Block-List");
    private static boolean enableDestroyEvent = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
    private static ConfigurationSection destroyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Block-List");

    private static Integer cooldownSeconds = ConfigHandler.getConfig("config.yml").getInt("See.Cooldown");
    private static Map< String, Long > playersOnCooldown = new HashMap< String, Long >();

    @EventHandler
    public void onclickBlock(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        //Ignore creative players.
         if (player.getGameMode() != GameMode.SURVIVAL) {
             return;
         }

        Material itemOnHand = player.getInventory().getItemInMainHand().getType();
        String itemOnHandString = player.getInventory().getItemInMainHand().getType().toString();
        //Holding a menu item.
        if (itemOnHand == Material.getMaterial(menuItemType)) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(menuItemName) || menuItemName.equals("")) {
                //Left click a block.
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Material clickBlock = e.getClickedBlock().getBlockData().getMaterial();
                    String clickBlockString = clickBlock.toString();
                    Location clickBlockLoc = e.getClickedBlock().getLocation();
                    //Check if player is sneaking.
                    if (player.isSneaking() == false) {
                        //Display near barriers and structure_void.
                        if (enableSeeEvent == true) {
                            /*
                            if (onCooldown(e.getPlayer())) {
                                Language.sendLangMessage("Message.Cooldown", player);
                                return;
                            }

                             */
                            if (seeBlockList.getKeys(false).contains(clickBlockString)) {
                                String seeBlockType = clickBlockString;
                                if (PermissionsHandler.hasPermission(player, "barrierplus.see." + seeBlockType.toLowerCase()) ||
                                        PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                                    checkBarrier(player, seeBlockType);
                                }
                            }
                        }
                    } else {
                        if (enableDestroyEvent == true) {
                            if (destroyBlockList.getKeys(false).contains(clickBlockString)) {
                                //Residence - Enable.
                                if (ConfigHandler.getDepends().ResidenceEnabled() == true) {
                                    ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(clickBlockLoc);
                                    //Residence - In protect area.
                                    if (res != null) {
                                        ResidencePermissions perms = res.getPermissions();
                                        boolean hasPermBuild = perms.playerHas(player, Flags.build, true);
                                        //Residence - Has build permission.
                                        if (!hasPermBuild) {
                                            if (!PermissionsHandler.hasPermission(player, "residence.bypass.build") && !player.isOp()) {
                                                Language.sendLangMessage("Message.No-Perm-destroy", player);
                                                return;
                                            }
                                        }
                                    }
                                    if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + clickBlockString.toLowerCase()) ||
                                            PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                                        //Destroy the block.
                                        Location blockLocation = e.getClickedBlock().getLocation();
                                        blockLocation.getBlock().setType(Material.AIR);
                                        player.getWorld().dropItem(blockLocation, new ItemStack(Material.getMaterial(clickBlockString)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Item's material on hand: BARRIER, STRUCTURE_VOID
        else if (enableSeeEvent == true) {
            /*
            if (onCooldown(e.getPlayer())) {
                Language.sendLangMessage("Message.Cooldown", player);
                return;
            }

             */
            if (seeBlockList.getKeys(false).contains(itemOnHandString)) {
                String seeBlockType = itemOnHandString;
                //Left-click "AIR".
                if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                    //Display near barriers and structure_void.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.see." + seeBlockType.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                        checkBarrier(player, seeBlockType);
                    }
                }
            }
        }
    }

    //Display near barriers and structure_void.
    public static void checkBarrier(Player player, String seeBlockType) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Set<Location> locations = new HashSet();
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Location loc = player.getLocation().getBlock().getLocation().clone().add(x, y, z);
                        if (loc.getBlock().getType() == Material.getMaterial(seeBlockType)) {
                            locations.add(loc.add(0.5D, 0.5D, 0.5D));
                        }
                    }
                }
            }
            String particleType = ConfigHandler.getConfig("config.yml").getString("See.Block-List." + seeBlockType + ".Particle");
            int particleAmount = ConfigHandler.getConfig("config.yml").getInt("See.Block-List." + seeBlockType + ".Amount");
            long particleTimes = ConfigHandler.getConfig("config.yml").getLong("See.Block-List." + seeBlockType + ".Times");
            particleTimes *= 20;
            long particleDelay = ConfigHandler.getConfig("config.yml").getLong("See.Block-List." + seeBlockType + ".Delay-Tick");

            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BarrierPlus.getInstance(), new Runnable() {
                public void run() {
                    locations.stream().forEach((loc) -> player.spawnParticle(Particle.valueOf(particleType), loc, particleAmount, 0, 0, 0, 0));
                }
            }, particleDelay, particleTimes);
        }
    }
/*
    private static boolean onCooldown(Player player) {
        int cdMillis = cooldownSeconds * 1000;
        long playersCooldownList = 0L;
        if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + player.getName());
        }

        if (System.currentTimeMillis() - playersCooldownList >= cdMillis) { return false; }
        return true;
    }

 */
}