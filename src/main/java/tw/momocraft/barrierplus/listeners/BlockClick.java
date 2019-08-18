package tw.momocraft.barrierplus.listeners;

import com.bekvon.bukkit.residence.Residence;
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
import java.util.Set;

public class BlockClick implements Listener {

    private static int RANGE = ConfigHandler.getConfig("config.yml").getInt("Barrier-Show-Distance");
    private static String MenuItemType = ConfigHandler.getConfig("config.yml").getString("Menu-Item-Type");
    private static String MenuItemName = ConfigHandler.getConfig("config.yml").getString("Menu-Item-Name");

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        //手上的方塊: 選單 | Holding menu item.
        if (player.getInventory().getItemInMainHand().getType() == Material.getMaterial(MenuItemType)) {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(MenuItemName) || MenuItemName.equals("")) {

                //左鍵-方塊 | Left click a block
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        Material ClickBlock = e.getClickedBlock().getBlockData().getMaterial();
                        Location ClickBlockLoc = e.getClickedBlock().getLocation();

                        //點擊方塊: 屏障 | The block's material which player click.
                        if (ClickBlock == Material.BARRIER) {
                            //檢查玩家是否蹲下 | Check if player is sneaking
                            if (player.isSneaking() == false) {
                                if (PermissionsHandler.hasPermission(player, "barrierplus.barrier.see")) {
                                    checkBarrier(player);
                                }
                            } else {
                                //檢查Residence插件 | Has plugin "Residnece".
                                if (ConfigHandler.getDepends().ResidenceEnabled() == true) {
                                    ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(ClickBlockLoc);
                                    //檢查有無保護區 | In a residence
                                    if (res != null) {
                                        ResidencePermissions perms = res.getPermissions();
                                        boolean hasPermission = perms.playerHas(player, "destroy", true);
                                        //檢查有無保護區-破壞權限 | Has build permission
                                        if (!hasPermission && !PermissionsHandler.hasPermission(player, "residence.bypass.build") && !player.isOp() ) {
                                            Language.sendLangMessage("Message.No-Perm-destroy", player);
                                            return;
                                        }
                                    }
                                }
                                //檢查有無破壞權限 | Has build permission.
                                if (PermissionsHandler.hasPermission(player, "barrierplus.barrier.destroy")) {
                                    //破壞屏障 | Destroy barrier
                                    Location blockLocation = e.getClickedBlock().getLocation();
                                    blockLocation.getBlock().setType(Material.AIR);
                                    player.getWorld().dropItem(blockLocation, new ItemStack(Material.BARRIER));
                                }
                            }
                        }
                    }
                }
            }

            //手上的方塊: 屏障 | Item's material on hand: BARRIER
            else if (player.getInventory().getItemInMainHand().getType() == Material.BARRIER) {

                //左鍵-方塊、空氣 | Left-click "AIR"
                if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                    //查看屏障位置 | Display barriers
                    if (PermissionsHandler.hasPermission(player, "barrierplus.barrier.see")) {
                        checkBarrier(player);
                    }
                }
            }
        }

    //查看屏障位置 | Display barriers.
    private static void checkBarrier(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            Set<Location> locations = new HashSet();
            for (int x = -RANGE; x <= RANGE; x++) {
                for (int y = -RANGE; y <= RANGE; y++) {
                    for (int z = -RANGE; z <= RANGE; z++) {
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