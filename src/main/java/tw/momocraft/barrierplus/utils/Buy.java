package tw.momocraft.barrierplus.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.PlayerHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;


public class Buy {

    public static void giveItem(CommandSender sender, Player target, String item, int amount) {
        String senderName = sender.getName();
        Material material;
        try {
            material = Material.getMaterial(item);
        } catch (Exception e) {
            Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
            ServerHandler.sendFeatureMessage("Give", senderName, "noShopItem", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        if (!PermissionsHandler.hasPermission(sender, "barrierplus.give." + item)) {
            Language.sendLangMessage("Message.BarrierPlus.buyNoPerm", sender);
            ServerHandler.sendFeatureMessage("Give", senderName, "buyNoPerm", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        Player player;
        if (target == null) {
            if (sender instanceof ConsoleCommandSender) {
                Language.dispatchMessage(sender, "&c/barrierplus give <item> [player] [amount]", true);
                ServerHandler.sendFeatureMessage("Give", senderName, "ConsoleCommandSender", "fail", item,
                        new Throwable().getStackTrace()[0]);
                return;
            }
            player = (Player) sender;
        } else {
            player = target;
        }
        int itemMaxSize = material.getMaxStackSize();
        int itemStacks = (int) Math.ceil(amount / itemMaxSize);
        int itemRemain = amount % itemMaxSize;
        if (getInventoryFull(player, material, itemMaxSize, itemStacks, itemRemain)) {
            Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
            ServerHandler.sendFeatureMessage("Give", senderName, "getInventoryFull", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        String[] placeHolders = Language.newString();
        addItem(player, material, itemMaxSize, itemStacks, itemRemain);
        placeHolders[7] = item;
        placeHolders[6] = String.valueOf(amount);
        Language.sendLangMessage("Message.BarrierPlus.giveSuccess", player, placeHolders);
        if (target != null) {
            placeHolders[2] = sender.getName();
            Language.sendLangMessage("Message.BarrierPlus.giveTargetSuccess", sender, placeHolders);
        }
        ServerHandler.sendFeatureMessage("Give", senderName, "final", "success", item,
                new Throwable().getStackTrace()[0]);
    }

    public static void buyItem(CommandSender sender, Player target, String item) {
        String senderName = sender.getName();
        if (ConfigHandler.getDepends().getVault().getEconomy() == null && !ConfigHandler.getDepends().PlayerPointsEnabled()) {
            Language.dispatchMessage(sender, "&cCan not find Vault or Economy plugin.", true);
            ServerHandler.sendFeatureMessage("Buy", senderName, "plugin", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        Material material;
        try {
            material = Material.getMaterial(item);
        } catch (Exception e) {
            Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
            ServerHandler.sendFeatureMessage("Buy", senderName, "noShopItem", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        BuyMap buyMap = ConfigHandler.getConfigPath().getBuyProp().get(item);
        if (buyMap == null) {
            Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
            ServerHandler.sendFeatureMessage("Buy", senderName, "noShopItem", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        if (!PermissionsHandler.hasPermission(sender, "barrierplus.buy." + item)) {
            Language.sendLangMessage("Message.BarrierPlus.buyNoPerm", sender);
            ServerHandler.sendFeatureMessage("Buy", senderName, "buyNoPerm", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        int amount = buyMap.getAmount();
        String priceType = buyMap.getPriceType();
        int price = (int) buyMap.getPrice();
        if (priceType == null) {
            Language.dispatchMessage(sender, "&cCan not find the price type of &e" + item + "&c in config.");
            ServerHandler.sendFeatureMessage("Buy", senderName, "priceType", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        Player player;
        if (target == null) {
            if (sender instanceof ConsoleCommandSender) {
                Language.dispatchMessage(sender, "&c/barrierplus buy <item> [player]", true);
                ServerHandler.sendFeatureMessage("Buy", senderName, "ConsoleCommandSender", "fail", item,
                        new Throwable().getStackTrace()[0]);
                return;
            }
            player = (Player) sender;
        } else {
            player = target;
        }
        double balance = PlayerHandler.getTypeBalance(player, priceType);
        if (balance < price) {
            String[] placeHolders = Language.newString();
            placeHolders[7] = item;
            placeHolders[6] = String.valueOf(amount);
            placeHolders[3] = priceType;
            placeHolders[4] = String.valueOf(price);
            placeHolders[5] = String.valueOf(balance);
            Language.sendLangMessage("Message.BarrierPlus.buyNotEnoughMoney", player, placeHolders);
            if (target != null) {
                placeHolders[2] = sender.getName();
                Language.sendLangMessage("Message.BarrierPlus.buyTargetSuccess", sender, placeHolders);
            }
            ServerHandler.sendFeatureMessage("Buy", senderName, "balance", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        int itemMaxSize = material.getMaxStackSize();
        int itemStacks = (int) Math.ceil(amount / itemMaxSize);
        int itemRemain = amount % itemMaxSize;
        if (getInventoryFull(player, material, itemMaxSize, itemStacks, itemRemain)) {
            Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
            ServerHandler.sendFeatureMessage("Buy", senderName, "getInventoryFull", "fail", item,
                    new Throwable().getStackTrace()[0]);
            return;
        }
        PlayerHandler.takeTypeMoney(player, priceType, price);
        String[] placeHolders = Language.newString();
        addItem(player, material, itemMaxSize, itemStacks, itemRemain);
        placeHolders[7] = item;
        placeHolders[6] = String.valueOf(amount);
        placeHolders[3] = priceType;
        placeHolders[4] = String.valueOf(price);
        placeHolders[5] = String.valueOf(balance);
        Language.sendLangMessage("Message.BarrierPlus.buySuccess", player, placeHolders);
        if (target != null) {
            placeHolders[2] = sender.getName();
            Language.sendLangMessage("Message.BarrierPlus.buyTargetSuccess", sender, placeHolders);
        }
        ServerHandler.sendFeatureMessage("Buy", senderName, "final", "success", item,
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
