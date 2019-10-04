package tw.momocraft.barrierplus.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockClick implements Listener {

    private static int range = ConfigHandler.getConfig("config.yml").getInt("Barrier-Show-Distance");
    private static String menuItemType = ConfigHandler.getConfig("config.yml").getString("Menu-Item-Type");
    private static String menuItemName = ConfigHandler.getConfig("config.yml").getString("Menu-Item-Name").replace("&", "§");
    private static boolean enableDestroyEvent = ConfigHandler.getConfig("config.yml").getBoolean("Destroy-Event");
    private static List<String> destroyBlockList = ConfigHandler.getConfig("config.yml").getStringList("Destroy-Block-List");

    @EventHandler
    public void onclickBlock(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        //Holding a menu item.
        if (player.getInventory().getItemInMainHand().getType() == Material.getMaterial(menuItemType)) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(menuItemName) || menuItemName.equals("")) {
                //Left click a block.
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Material clickBlock = e.getClickedBlock().getBlockData().getMaterial();
                    String clickBlockString = clickBlock.toString();
                    Location clickBlockLoc = e.getClickedBlock().getLocation();
                    //Check if player is sneaking.
                    if (player.isSneaking() == false) {
                        //The block's material which player click.
                        if (clickBlock == Material.BARRIER) {
                            if (PermissionsHandler.hasPermission(player, "barrierplus.see.barrier") ||
                            PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                                checkBarrier(player);
                            }
                        }
                    } else {
                        if (enableDestroyEvent == true) {
                            if (destroyBlockList.contains(clickBlockString)) {
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
        //Item's material on hand: BARRIER
        else if (player.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
            //Left-click "AIR".
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                //Display near barriers.
                if (PermissionsHandler.hasPermission(player, "barrierplus.see.barrier") ||
                        PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                    checkBarrier(player);
                }
            }
        }
    }

    //Display near barriers.
    private static void checkBarrier(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Set<Location> locations = new HashSet();
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Location l = player.getLocation().getBlock().getLocation().clone().add(x, y, z);
                        if (l.getBlock().getType() == Material.BARRIER) {
                            locations.add(l.add(0.5D, 0.5D, 0.5D));
                        }
                    }
                }
            }
            locations.stream().forEach((l) -> player.spawnParticle(Particle.BARRIER, l, 1));
        }
    }
}