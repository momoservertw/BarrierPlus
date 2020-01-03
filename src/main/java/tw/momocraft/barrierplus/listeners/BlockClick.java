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
import org.bukkit.scheduler.BukkitRunnable;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;
import tw.momocraft.barrierplus.utils.LocationAPI;

import java.util.*;

public class BlockClick implements Listener {

    private Map<String, Long> cooldownShowList = new HashMap<String, Long>();
    private Map<String, Long> cooldownDestroyList = new HashMap<String, Long>();

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        // Holding a menu item.
        String itemOnHand = player.getInventory().getItemInMainHand().getType().name();
        if (player.getInventory().getItemInMainHand().getType().name().equals(ConfigHandler.getConfig("config.yml").getString("Menu.Item-Type"))) {
            String menuItemName = ConfigHandler.getConfig("config.yml").getString("Menu.Item-Name");
            if (menuItemName.equals("") || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(menuItemName.replace("&", "§"))) {
                // Left click a block.
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    String block = e.getClickedBlock().getBlockData().getMaterial().name();
                    Location clickBlockLoc = e.getClickedBlock().getLocation();
                    // Player isn't sneaking.
                    if (!player.isSneaking()) {
                        // See - Display near blocks.
                        if (ConfigHandler.getConfig("config.yml").getBoolean("See.Enable")) {
                            ConfigurationSection seeList = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.List");
                            if (seeList != null && seeList.getKeys(false).contains(block)) {
                                // Player is on cooldown.
                                if (onCooldownSee(player)) {
                                    if (ConfigHandler.getConfig("config.yml").getBoolean("See.Cooldown-Message")) {
                                        Language.sendLangMessage("Message.cooldown", player);
                                    }
                                    ServerHandler.debugMessage("(BlockClick) See", "show particle", "cooldown = true", "cancel");
                                    return;
                                }
                                // Has see permission.
                                if (PermissionsHandler.hasPermission(player, "barrierplus.see." + block.toLowerCase()) ||
                                        PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                                    addCooldownSee(player);
                                    checkBlock(player, block);
                                    return;
                                }
                                ServerHandler.debugMessage("(BlockClick) See", block, "final", "return");
                            }
                        }
                    } else {
                        // Destroy - Break block by menu.
                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
                            if (ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List").getKeys(false).contains(block)) {
                                String menuBreakEnable = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Menu-Break");
                                if (menuBreakEnable == null || menuBreakEnable.equals("true")) {
                                    // Player is on cooldown.
                                    if (onCooldownDestroy(player)) {
                                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Menu-Break.Cooldown-Message")) {
                                            Language.sendLangMessage("Message.cooldown", player);
                                        }
                                        ServerHandler.debugMessage("(BlockClick) Destroy", "Menu-Break", "cooldown = true", "return");
                                        return;
                                    }
                                    // Residence - Enable.
                                    if (ConfigHandler.getDepends().ResidenceEnabled()) {
                                        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(clickBlockLoc);
                                        // Residence - In protect area.
                                        if (res != null) {
                                            ResidencePermissions perms = res.getPermissions();
                                            boolean hasPermBuild = perms.playerHas(player, Flags.build, true);
                                            // Residence - Has build permission.
                                            if (!hasPermBuild) {
                                                if (!PermissionsHandler.hasPermission(player, "residence.bypass.build")) {
                                                    Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                                                    ServerHandler.debugMessage("(BlockClick) Destroy-Menu", block, "residence permission = false", "return");
                                                    return;
                                                }
                                            }
                                        }
                                        // Has destroy bypass permission.
                                        if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.destroy")) {
                                            addCooldownDestroy(player);
                                            Location blockLocation = e.getClickedBlock().getLocation();
                                            blockLocation.getBlock().setType(Material.AIR);
                                            ServerHandler.debugMessage("(BlockClick) Destroy", block, "has bypass permission", "bypass");
                                            String drop = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Menu-Drop");
                                            // Block - Menu-Drop = true
                                            if (drop == null || drop.equals("true")) {
                                                player.getWorld().dropItem(blockLocation, new ItemStack(Material.getMaterial(block)));
                                                ServerHandler.debugMessage("(BlockClick) Destroy", block, "Menu-Drop = true", "return");
                                                return;
                                            }
                                        }
                                        // Has destroy permission.
                                        if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + block.toLowerCase()) ||
                                                PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                                            // The location can place the block.
                                            if (LocationAPI.getLocation(e.getClickedBlock(), "Destroy.List." + block + ".Location")) {
                                                addCooldownDestroy(player);
                                                Location blockLocation = e.getClickedBlock().getLocation();
                                                blockLocation.getBlock().setType(Material.AIR);
                                                String drop = ConfigHandler.getConfig("config.yml").getString("Destroy.List." + block + ".Menu-Drop");
                                                // Block - Menu-Drop = true
                                                if (drop == null || drop.equals("true")) {
                                                    player.getWorld().dropItem(blockLocation, new ItemStack(Material.getMaterial(block)));
                                                    ServerHandler.debugMessage("(BlockClick) Destroy", block, "Menu-Drop = true", "return");
                                                    return;
                                                }
                                            // The location cannot place the block.
                                            } else {
                                                String[] placeHolders = Language.newString();
                                                placeHolders[7] = block;
                                                Language.sendLangMessage("Message.BarrierPlus.breakLocFail", player, placeHolders);
                                                ServerHandler.debugMessage("(BlockBreak) Destroy", block, "Location = false", "return");
                                                return;
                                            }
                                        }
                                    }
                                }
                                ServerHandler.debugMessage("(BlockClick) Destroy", block, "final", "return");
                            }
                        }
                    }
                }
            }
        // Holding a creative block.
        } else if (ConfigHandler.getConfig("config.yml").getBoolean("See.Enable")) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (ConfigHandler.getConfig("config.yml").getConfigurationSection("See.List").getKeys(false).contains(itemOnHand)) {
                    if (onCooldownSee(e.getPlayer())) {
                        if (ConfigHandler.getConfig("config.yml").getBoolean("See.Cooldown-Message")) {
                            Language.sendLangMessage("Message.cooldown", player);
                        }
                        ServerHandler.debugMessage("(BlockClick) See", itemOnHand, "cooldown = true", "return");
                        return;
                    }
                    if (PermissionsHandler.hasPermission(player, "barrierplus.see." + itemOnHand.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.see.*")) {
                        addCooldownSee(player);
                        checkBlock(player, itemOnHand);
                        return;
                    }
                    ServerHandler.debugMessage("(BlockClick) See", itemOnHand, "final", "return");
                }
            }
        }
    }

    /**
     *
     *  Display nearby creative blocks like barriers.
     *
     * @param player the trigger player.
     * @param block the display creative blocks.
     */
    private void checkBlock(Player player, String block) {
        String enableCreature = ConfigHandler.getConfig("config.yml").getString("See." + block + ".Creative-Mode");
        if (enableCreature != null && enableCreature.equals("false")) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                ServerHandler.debugMessage("(BlockClick) See", block, "Creative-Mode = false", "return");
                return;
            }
        }
        Set<Location> locations = new HashSet();
        int range = ConfigHandler.getConfig("config.yml").getInt("See.Distance");

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
        String particleType = ConfigHandler.getConfig("config.yml").getString("See.List." + block + ".Particle");
        int particleAmount = ConfigHandler.getConfig("config.yml").getInt("See.List." + block + ".Amount");
        long particleTimes = ConfigHandler.getConfig("config.yml").getLong("See.List." + block + ".Times");
        long particleInterval = ConfigHandler.getConfig("config.yml").getLong("See.List." + block + ".Interval-Tick");
        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                if (i > particleTimes) {
                    ServerHandler.debugMessage("(BlockClick) See", block, "Show particle", "cancel");
                    cancel();
                } else {
                    ++i;
                    locations.forEach((loc) -> player.spawnParticle(Particle.valueOf(particleType), loc, particleAmount, 0, 0, 0, 0));
                    ServerHandler.debugMessage("(BlockClick) See", block, "Show particle", "continue");
                }
            }
        }.runTaskTimer(BarrierPlus.getInstance(), 0, particleInterval);
    }

    private boolean onCooldownSee(Player player) {
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

    private void addCooldownSee(Player player) {
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