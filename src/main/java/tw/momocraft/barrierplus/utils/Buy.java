package tw.momocraft.barrierplus.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;


public class Buy {

    public static void buyItem(CommandSender sender, Player target, String item) {
        String senderName = sender.getName();
        Player player;
        if (target == null) {
            if (sender instanceof ConsoleCommandSender) {
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.onlyPlayer", sender);
                return;
            }
            player = (Player) sender;
        } else {
            player = target;
        }
        Material material;
        try {
            material = Material.getMaterial(item);
        } catch (Exception e) {
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noShopItem", sender);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "noShopItem", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        BuyMap buyMap = ConfigHandler.getConfigPath().getBuyProp().get(item);
        if (buyMap == null) {
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noShopItem", sender);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "noShopItem", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        if (!CorePlusAPI.getPlayerManager().hasPerm(ConfigHandler.getPluginName(), sender, "barrierplus.buy." + item) &&
                CorePlusAPI.getPlayerManager().hasPerm(ConfigHandler.getPluginName(), sender, "barrierplus.buy.*")) {
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "noPermission", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        String priceType = buyMap.getPriceType();
        switch (priceType) {
            case "money":
                if (CorePlusAPI.getDependManager().VaultEnabled() && CorePlusAPI.getDependManager().VaultEconEnabled()) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&cCan not find plugin: Vault or Economy plugin");
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "Vault", "fail", item,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                break;
            case "points":
                if (!CorePlusAPI.getDependManager().PlayerPointsEnabled()) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&cCan not find plugin: PlayerPoints");
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "PlayerPoints", "fail", item,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                break;
            default:
                if (!CorePlusAPI.getDependManager().GemsEconomyEnabled()) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&cCan not find plugin: GemsEconomy or Unknown Economy plugin");
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "GemsEconomy", "fail", item,
                            new Throwable().getStackTrace()[0]);
                    return;
                }
                break;
        }
        int amount = buyMap.getAmount();
        int price = (int) buyMap.getPrice();
        double balance = CorePlusAPI.getPlayerManager().getTypeBalance(player.getUniqueId(), priceType);
        if (balance < price) {
            String[] placeHolders = CorePlusAPI.getLangManager().newString();
            placeHolders[9] = priceType; // %pricetype%
            placeHolders[10] = String.valueOf(price); // %price%
            placeHolders[11] = String.valueOf(balance); // %balance%
            placeHolders[6] = String.valueOf(amount); // %amount%
            placeHolders[7] = item; // %material%
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.buyNotEnoughMoney", player, placeHolders);
            if (target != null) {
                placeHolders[1] = player.getName(); // %targetplayer%
                CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.buyTargetSuccess", sender, placeHolders);
            }
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "balance", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        int itemMaxSize = material.getMaxStackSize();
        int itemStacks = (int) Math.ceil(amount / itemMaxSize);
        int itemRemain = amount % itemMaxSize;
        if (getInventoryFull(player, material, itemMaxSize, itemStacks, itemRemain)) {
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.inventoryFull", sender);
            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "getInventoryFull", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        balance = CorePlusAPI.getPlayerManager().takeTypeMoney(player.getUniqueId(), priceType, price);
        String[] placeHolders = CorePlusAPI.getLangManager().newString();
        addItem(player, material, itemMaxSize, itemStacks, itemRemain);
        placeHolders[9] = priceType; // %pricetype%
        placeHolders[10] = String.valueOf(price);  // %price%
        placeHolders[11] = String.valueOf(balance); // %balance%
        placeHolders[6] = String.valueOf(amount); // %amount%
        placeHolders[7] = item; // %material%
        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.buySuccess", player, placeHolders);
        if (target != null) {
            placeHolders[2] = player.getName(); // %targetplayer%
            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.buyTargetSuccess", sender, placeHolders);
        }
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPlugin(), "Buy", senderName, "final", "success", item,
                new Throwable().getStackTrace()[0]);
    }

    private static boolean getInventoryFull(Player player, Material material, int itemMaxSize, int itemStacks, int itemRemain) {
        int slotEmpty = 0;
        int slotRemain = 0;
        ItemStack slotItem;
        for (int i = 0; i <= 35; i++) {
            slotItem = player.getInventory().getItem(i);
            if (slotItem == null) {
                slotEmpty++;
            } else {
                if (slotItem.getType() == material) {
                    slotRemain += itemMaxSize - slotItem.getAmount();
                    if (slotRemain >= itemMaxSize) {
                        slotRemain -= itemMaxSize;
                        slotEmpty++;
                    }
                }
            }
        }
        if (slotEmpty < itemStacks) {
            return true;
        }
        return slotRemain < itemRemain;
    }

    private static void addItem(Player player, Material material, int itemMaxStackSize, int itemStacks, int itemRemain) {
        for (int i = 1; i <= itemStacks; i++) {
            player.getInventory().addItem(new ItemStack(material, itemMaxStackSize));
        }
        player.getInventory().addItem(new ItemStack(material, itemRemain));
    }
}
