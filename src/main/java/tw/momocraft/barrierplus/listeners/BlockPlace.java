package tw.momocraft.barrierplus.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;
import tw.momocraft.barrierplus.utils.Language;

import java.util.List;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable")) {
            Player player = e.getPlayer();
            String block = e.getBlockPlaced().getBlockData().getMaterial().name();
            List<String> placeList = ConfigHandler.getConfig("config.yml").getStringList("Place.List");
            if (!placeList.isEmpty()) {
                if (ConfigHandler.getConfig("config.yml").getStringList("Place.List").contains(block)) {
                    // Has bypass permission.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.place")) {
                        ServerHandler.debugMessage("(BlockPlace) Place", block, "has bypass permission", "bypass");
                        return;
                    }
                    //Check placing permissions.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.place." + block.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                        String[] placeHolders = Language.newString();
                        placeHolders[7] = block;
                        Language.sendLangMessage("Message.BarrierPlus.placeLocFail", player, placeHolders);
                        ServerHandler.debugMessage("(BlockPlace) Place", block, "Location = false", "return");
                        e.setCancelled(true);
                        return;
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noPermPlace", player);
                    ServerHandler.debugMessage("(BlockPlace) Place", block, "permission", "cancel");
                    e.setCancelled(true);
                    return;
                }
                return;
            }
            ConfigurationSection placeConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.List");
            if (placeConfig != null) {
                if (placeConfig.getKeys(false).contains(block)) {
                    // Has bypass permission.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.bypass.place")) {
                        ServerHandler.debugMessage("(BlockPlace) Place", block, "has bypass permission", "bypass");
                        return;
                    }
                    //Check placing permissions.
                    if (PermissionsHandler.hasPermission(player, "barrierplus.place." + block.toLowerCase()) ||
                            PermissionsHandler.hasPermission(player, "barrierplus.place.*")) {
                        if (LocationAPI.getLocation(e.getBlock(), "Place.List." + block + ".Location")) {
                            ServerHandler.debugMessage("(BlockPlace) Place", block, "permission", "bypass");
                            return;
                        } else {
                            String[] placeHolders = Language.newString();
                            placeHolders[7] = block;
                            Language.sendLangMessage("Message.BarrierPlus.placeLocFail", player, placeHolders);
                            ServerHandler.debugMessage("(BlockPlace) Place", block, "Location = false", "return");
                            e.setCancelled(true);
                            return;
                        }
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noPermPlace", player);
                    ServerHandler.debugMessage("(BlockPlace) Place", block, "permission", "cancel");
                    e.setCancelled(true);
                }
            }
        }
    }
}
