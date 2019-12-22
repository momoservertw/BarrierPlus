package tw.momocraft.barrierplus.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.*;
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
import tw.momocraft.barrierplus.utils.Language;

import java.util.*;

public class BlockClick implements Listener {

    private Map<String, Long> cooldownShowList = new HashMap<String, Long>();
    private Map<String, Long> cooldownDestroyList = new HashMap<String, Long>();

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        String itemOnHand = player.getInventory().getItemInMainHand().getType().name();
        String menuItemName = ConfigHandler.getConfig("config.yml").getString("Menu.Item-Name");
        //Holding a menu item.
        if (itemOnHand.equals(ConfigHandler.getConfig("config.yml").getString("Menu.Item-Type"))) {
            if (menuItemName.equals("") || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(menuItemName.replace("&", "§"))) {
                //Left click a block.
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    String block = e.getClickedBlock().getBlockData().getMaterial().toString();
                    Location clickBlockLoc = e.getClickedBlock().getLocation();
                    //Check if player is sneaking.
                    if (!player.isSneaking()) {
                        //Display near barriers and structure_void.
                        if (ConfigHandler.getConfig("config.yml").getBoolean("See.Enable")) {
                            if (ConfigHandler.getConfig("config.yml").getConfigurationSection("See.List").getKeys(false).contains(block)) {
                                //Check if player is on cooldown.
                                if (onCooldownShow(player)) {
                                    if (ConfigHandler.getConfig("config.yml").getBoolean("See.Cooldown-Message")) {
                                        Language.sendLangMessage("Message.cooldown", player);
                                    }
                                    Language.debugMessage("See", "show particle", "cooldown = true", "return");
                                    return;
                                }
                                if (PermissionsHandler.hasPermission(player, "barrierplus.see." + block.toLowerCase()) ||
                                        PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                                    addCooldownShow(player);
                                    checkBlock(player, block);
                                }
                                Language.debugMessage("See", itemOnHand, "final", "return");
                            }
                        }
                    } else {
                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
                            if (ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List").getKeys(false).contains(block)) {
                                String menuBreakEnable = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Menu-Break");
                                if (menuBreakEnable == null || menuBreakEnable.equals("true")) {
                                    //Check if player is on cooldown.
                                    if (onCooldownDestroy(player)) {
                                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Menu-Break.Cooldown-Message")) {
                                            Language.sendLangMessage("Message.cooldown", player);
                                        }
                                        Language.debugMessage("Destroy", "Menu-Break", "cooldown = true", "return");
                                        return;
                                    }
                                    //Residence - Enable.
                                    if (ConfigHandler.getDepends().ResidenceEnabled()) {
                                        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(clickBlockLoc);
                                        //Residence - In protect area.
                                        if (res != null) {
                                            ResidencePermissions perms = res.getPermissions();
                                            boolean hasPermBuild = perms.playerHas(player, Flags.build, true);
                                            //Residence - Has build permission.
                                            if (!hasPermBuild) {
                                                if (!PermissionsHandler.hasPermission(player, "residence.bypass.build") && !player.isOp()) {
                                                    Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                                                    Language.debugMessage("Destroy-Menu", block, "residence permission = false", "return");
                                                    return;
                                                }
                                            }
                                        }
                                        if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + block.toLowerCase()) ||
                                                PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                                            addCooldownDestroy(player);
                                            Location blockLocation = e.getClickedBlock().getLocation();
                                            blockLocation.getBlock().setType(Material.AIR);

                                            String drop = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Menu-Drop");
                                            if (drop == null || drop.equals("true")) {
                                                player.getWorld().dropItem(blockLocation, new ItemStack(Material.getMaterial(block)));
                                                Language.debugMessage("Destroy", block, "Menu-Drop = true", "return");
                                                return;
                                            }
                                            Language.debugMessage("Destroy", block, "Menu-Drop = false", "return");
                                        }
                                    }
                                }
                                Language.debugMessage("Destroy", block, "final", "return");
                            }
                        }
                    }
                }
            }
        } else if (ConfigHandler.getConfig("config.yml").getBoolean("See.Enable")) {
            //Left-click "AIR".
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (ConfigHandler.getConfig("config.yml").getConfigurationSection("See.List").getKeys(false).contains(itemOnHand)) {
                    //Check if player is on cooldown.
                    if (onCooldownShow(e.getPlayer())) {
                        if (ConfigHandler.getConfig("config.yml").getBoolean("See.Cooldown-Message")) {
                            Language.sendLangMessage("Message.cooldown", player);
                        }
                        Language.debugMessage("See", itemOnHand, "cooldown = true", "return");
                        return;
                    }
                    //Display near barriers and structure_void.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.see." + itemOnHand.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                        addCooldownShow(player);
                        checkBlock(player, itemOnHand);
                    }
                    Language.debugMessage("See", itemOnHand, "final", "return");
                }
            }
        }
    }

    //Display near barriers and structure_void.
    private void checkBlock(Player player, String seeBlockType) {
        String enableCreature = ConfigHandler.getConfig("config.yml").getString("See." + seeBlockType + ".Creative-Mode");
        if (enableCreature != null && enableCreature.equals("false")) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                Language.debugMessage("See", seeBlockType, "Creative-Mode = false", "return");
                return;
            }
        }
        Set<Location> locations = new HashSet();
        int range = ConfigHandler.getConfig("config.yml").getInt("See.Distance");

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
        String particleType = ConfigHandler.getConfig("config.yml").getString("See.List." + seeBlockType + ".Particle");
        int particleAmount = ConfigHandler.getConfig("config.yml").getInt("See.List." + seeBlockType + ".Amount");
        long particleTimes = ConfigHandler.getConfig("config.yml").getLong("See.List." + seeBlockType + ".Times");
        long particleInterval = ConfigHandler.getConfig("config.yml").getLong("See.List." + seeBlockType + ".Interval-Tick");

        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                if (i > particleTimes) {
                    Language.debugMessage("See", seeBlockType, "Show particle", "cancel");
                    cancel();
                } else {
                    ++i;
                    locations.forEach((loc) -> player.spawnParticle(Particle.valueOf(particleType), loc, particleAmount, 0, 0, 0, 0));
                    Language.debugMessage("See", seeBlockType, "Show particle", "continue");
                }
            }
        }.runTaskTimer(BarrierPlus.getInstance(), 0, particleInterval);
    }

    private boolean onCooldownShow(Player player) {
        int cooldownTick = ConfigHandler.getConfig("config.yml").getInt("See.Cooldown");
        if (cooldownTick == 0) {
            return false;
        }
        int cdMillis = cooldownTick * 50;
        long playersCooldownList = 0L;
        if (cooldownShowList.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCooldownList = cooldownShowList.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCooldownList < cdMillis;
    }

    private void addCooldownShow(Player player) {
        cooldownShowList.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }

    private boolean onCooldownDestroy(Player player) {
        int cooldownTick = ConfigHandler.getConfig("config.yml").getInt("Destroy.Menu-Break.Cooldown");
        if (cooldownTick == 0) {
            return false;
        }
        int cdMillis = cooldownTick * 50;
        long playersCooldownList = 0L;
        if (cooldownDestroyList.containsKey(player.getWorld().getName() + "." + player.getName())) {
            playersCooldownList = cooldownDestroyList.get(player.getWorld().getName() + "." + player.getName());
        }
        return System.currentTimeMillis() - playersCooldownList < cdMillis;
    }

    private void addCooldownDestroy(Player player) {
        cooldownDestroyList.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }
}