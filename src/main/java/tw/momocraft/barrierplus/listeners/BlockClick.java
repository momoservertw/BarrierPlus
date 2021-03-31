package tw.momocraft.barrierplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.DestroyMap;
import tw.momocraft.barrierplus.utils.SeeMap;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockClick implements Listener {

    private final Map<String, Long> seeCDMap = new HashMap<>();
    private final Map<String, Long> destroyCDMap = new HashMap<>();

    /*
     * Destroy: Hold menu & shift + left-click (the bloc)
     *
     * See: Hold menu & left-click (the bloc)
     * See: Hold same item & left-click (AIR or the Bloc)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClickBlock(PlayerInteractEvent e) {
        boolean see = ConfigHandler.getConfigPath().isSee();
        boolean destroy = ConfigHandler.getConfigPath().isDestroy();
        if (!see && !destroy)
            return;
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
                if (!CorePlusAPI.getCond().isMenu(itemStack))
                    return;
                // Destroy - Break block by menu.
                if (ConfigHandler.getConfigPath().isDestroy())
                    destroyBlock(player, block.getLocation(), block);
            } else {
                // See - Display near blocks.
                if (ConfigHandler.getConfigPath().isSee()) {
                    if (!CorePlusAPI.getCond().isMenu(itemStack) &&
                            !itemStack.getType().name().equals(blockType))
                        return;
                    seeBlock(player, block);
                }
            }
            // Left click air.
        } else if (action.equals("LEFT_CLICK_AIR")) {
            if (ConfigHandler.getConfigPath().isSee())
                seeBlock(player, block);
        }
    }

    private void seeBlock(Player player, Block block) {
        String blockType = block.getType().name();
        SeeMap seeMap = ConfigHandler.getConfigPath().getSeeProp().get(blockType);
        if (seeMap == null)
            return;
        String playerName = player.getName();
        // Checking the "Conditions".
        List<String> conditionList = CorePlusAPI.getMsg().transHolder(player, block, seeMap.getConditions());
        if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPlugin(), conditionList)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Destroy", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Player is on cooldown.
        if (onSeeCD(player)) {
            if (ConfigHandler.getConfigPath().isSeeCDMsg())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "See", playerName, "cooldown", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Has see permission.
        if (CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see." + blockType) ||
                CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.see.*")) {
            addSeeCD(player);
            displayBlock(player, blockType, seeMap);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "See", playerName, "final", "return", blockType,
                    new Throwable().getStackTrace()[0]);
        }
    }

    private void destroyBlock(Player player, Location blockLoc, Block block) {
        String blockType = block.getType().name();
        DestroyMap destroyMap = ConfigHandler.getConfigPath().getDestroyProp().get(blockType);
        if (destroyMap == null)
            return;
        // Enable
        if (!destroyMap.isMenuBreak())
            return;
        String playerName = player.getName();
        // Cooldown
        if (onDestroyCD(player)) {
            if (ConfigHandler.getConfigPath().isDestroyCDMsg())
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                        "Message.cooldown", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "cooldown", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Checking the "Conditions".
        List<String> conditionList = CorePlusAPI.getMsg().transHolder(player, block, destroyMap.getConditions());
        if (!CorePlusAPI.getCond().checkCondition(ConfigHandler.getPlugin(), conditionList)) {
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPlugin(),
                    "Destroy", playerName, "Condition", "none", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Destroy permission
        if (!CorePlusAPI.getPlayer().hasPerm(player, "barrierplus.destroy." + blockType)) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                    "Message.noPermission", player);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "permission", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Residence flag
        if (!CorePlusAPI.getCond().checkFlag(player, blockLoc, "destroy", false, true)) {
            String[] placeHolders = CorePlusAPI.getMsg().newString();
            placeHolders[13] = "destroy"; // %flag%
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPlugin(), ConfigHandler.getPrefix(),
                    "Message.noFlagPerm", player, placeHolders);
            CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                    "Destroy", playerName, "residence: destroy=false", "return", blockType,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        // Menu Drop
        if (destroyMap.isMenuDrop()) {
            try {
                player.getWorld().dropItem(blockLoc, new ItemStack(Material.getMaterial(blockType)));
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                        "Destroy", playerName, "Drop", "return", blockType,
                        new Throwable().getStackTrace()[0]);
            } catch (Exception ex) {
                CorePlusAPI.getMsg().sendDebugTrace(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(), ex);
            }
        }
        // Destroy the block.
        addDestroyCD(player);
        blockLoc.getBlock().setType(Material.AIR);
        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginPrefix(),
                "Destroy", playerName, "none", "return", blockType,
                new Throwable().getStackTrace()[0]);
    }

    private void displayBlock(Player player, String block, SeeMap seeMap) {
        int range = ConfigHandler.getConfigPath().getSeeDistance();
        Location playerLoc = player.getLocation();
        Location loc;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    loc = playerLoc.clone().add(x, y, z);
                    if (loc.getBlock().getType().equals(block))
                        CorePlusAPI.getCmd().dispatchParticleGroup(ConfigHandler.getPrefix(), loc, seeMap.getParticle());
                }
            }
        }
    }

    private boolean onSeeCD(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getSeeCDInterval();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (seeCDMap.containsKey(player.getWorld().getName() + "." + player.getName()))
            playersCDList = seeCDMap.get(player.getWorld().getName() + "." + player.getName());
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addSeeCD(Player player) {
        seeCDMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }

    private boolean onDestroyCD(Player player) {
        int cdTick = ConfigHandler.getConfigPath().getDestroyCD();
        if (cdTick == 0)
            return false;
        int cdMillis = cdTick * 50;
        long playersCDList = 0L;
        if (destroyCDMap.containsKey(player.getWorld().getName() + "." + player.getName()))
            playersCDList = destroyCDMap.get(player.getWorld().getName() + "." + player.getName());
        return System.currentTimeMillis() - playersCDList < cdMillis;
    }

    private void addDestroyCD(Player player) {
        destroyCDMap.put(player.getWorld().getName() + "." + player.getName(), System.currentTimeMillis());
    }
}