package tw.momocraft.barrierplus.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;
import tw.momocraft.barrierplus.utils.LocationAPI;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable")) {
            Player player = e.getPlayer();
            String block = e.getBlock().getBlockData().getMaterial().name();
            ConfigurationSection blockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.List");
            if (blockList != null) {
                if (blockList.getKeys(false).contains(block)) {
                    // Has bypass permission.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.destroy")) {
                        ServerHandler.debugMessage("(BreakBlock) Destroy", block, "has bypass permission", "bypass");
                        return;
                    }
                    // Cancel vanilla break event.
                    if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.List." + block + ".Vanilla-Break")) {
                        if (player.getInventory().getItemInMainHand().getType().name().equals(ConfigHandler.getConfig("config.yml").getString("Menu.Item-Type"))) {
                            String menuItemName = ConfigHandler.getConfig("config.yml").getString("Menu.Item-Name");
                            if (menuItemName.equals("") || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(menuItemName.replace("&", "§"))) {
                                e.setCancelled(true);
                                return;
                            }
                        }
                        if (ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Menu-Break.Help-Message")) {
                            Language.sendLangMessage("Message.BarrierPlus.breakHelp", player);
                        }
                        ServerHandler.debugMessage("(BlockBreak) Destroy", block, "Vanilla-Break", "cancel");
                        e.setCancelled(true);
                        return;
                    }
                    // Has destroy permission.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.destroy." + block.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.destroy.*")) {
                        // In a legal location.
                        if (LocationAPI.getLocation(e.getBlock(), "Destroy.List." + block + ".Location")) {
                            ServerHandler.debugMessage("(BlockBreak) Destroy", block, "location", "return");
                            return;
                        } else {
                            String[] placeHolders = Language.newString();
                            placeHolders[7] = block;
                            Language.sendLangMessage("Message.BarrierPlus.breakLocFail", player, placeHolders);
                            ServerHandler.debugMessage("(BlockBreak) Destroy", block, "Location = false", "return");
                            e.setCancelled(true);
                            return;
                        }
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noPermDestroy", player);
                    ServerHandler.debugMessage("(BlockBreak) Destroy", block, "permission = false", "cancel");
                    e.setCancelled(true);
                }
            }
        }
    }
}
