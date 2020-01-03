package tw.momocraft.barrierplus;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.PlayerHandler;
import tw.momocraft.barrierplus.utils.Language;

public class Commands implements CommandExecutor {

    private static Economy econ;

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        if (args.length == 0) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.use")) {
                Language.sendLangMessage("Message.BarrierPlus.Commands.title", sender, false);
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.version")) {
                    Language.dispatchMessage(sender, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                }
                Language.sendLangMessage("Message.BarrierPlus.Commands.help", sender, false);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.use")) {
                Language.dispatchMessage(sender, "");
                Language.sendLangMessage("Message.BarrierPlus.Commands.title", sender, false);
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.version")) {
                    Language.dispatchMessage(sender, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                }
                Language.sendLangMessage("Message.BarrierPlus.Commands.help", sender, false);
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.reload")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.reload", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.buy", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy.other")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.buyOther", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.give", sender, false);
                }
                if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.giveOther", sender, false);
                }
                Language.dispatchMessage(sender, "");
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.reload")) {
                ConfigHandler.generateData();
                Language.sendLangMessage("Message.configReload", sender);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("version")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.version")) {
                Language.dispatchMessage(sender, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                ConfigHandler.getUpdater().checkUpdates(sender, false);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("buy")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy.other")) {
                Language.sendLangMessage("Message.BarrierPlus.Commands.buyOther", sender, false);
            } else if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy")) {
                Language.sendLangMessage("Message.BarrierPlus.Commands.buy", sender, false);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy")) {
                if (ConfigHandler.getDepends().getVault().getEconomy() == null && !ConfigHandler.getDepends().PlayerPointsEnabled()) {
                    Language.dispatchMessage(sender, "&cCan not find Vault or Economy plugin.", true);
                    return true;
                }
                if (!(sender instanceof ConsoleCommandSender)) {
                    ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                    String item = args[1].toUpperCase();
                    if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                        if (Material.getMaterial(item) != null) {
                            if (PermissionsHandler.hasPermission(sender, "barrierplus.buy." + item)) {
                                Player player = (Player) sender;
                                int amount = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Amount");
                                int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                                if (amount == 0) {
                                    amount = itemMaxStackSize;
                                }
                                String priceType = ConfigHandler.getConfig("config.yml").getString("Buy.List." + item + ".Price-Type");
                                int price = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Price");
                                if (priceType == null) {
                                    Language.dispatchMessage(sender, "&cCan not find the price type of &e" + item + "&c in config.");
                                    return true;
                                }
                                if (PlayerHandler.getEnoughMoney(player, priceType, price)) {
                                    int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                                    int itemRemain = amount % itemMaxStackSize;
                                    if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                        String[] placeHolders = Language.newString();
                                        double balance = PlayerHandler.takeTypeMoney(player, priceType, price);
                                        giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                        placeHolders[7] = item;
                                        placeHolders[6] = String.valueOf(amount);
                                        placeHolders[3] = priceType;
                                        placeHolders[4] = String.valueOf(price);
                                        placeHolders[5] = String.valueOf(balance);
                                        Language.sendLangMessage("Message.BarrierPlus.buySuccess", sender, placeHolders);
                                        return true;
                                    }
                                    Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
                                    return true;
                                }
                                String[] placeHolders = Language.newString();
                                placeHolders[7] = item;
                                placeHolders[6] = String.valueOf(amount);
                                placeHolders[3] = priceType;
                                placeHolders[4] = String.valueOf(price);
                                placeHolders[5] = String.valueOf(PlayerHandler.getTypeBalance(player, priceType));
                                Language.sendLangMessage("Message.BarrierPlus.buyNotEnoughMoney", player, placeHolders);
                                return true;
                            } else {
                                Language.sendLangMessage("Message.BarrierPlus.buyNoPerm", sender);
                            }
                        }
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                    return true;
                }
                Language.dispatchMessage(sender, "&c/barrierplus buy <item> [player]", true);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("buy")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy.other")) {
                if (ConfigHandler.getDepends().getVault().getEconomy() == null && !ConfigHandler.getDepends().PlayerPointsEnabled()) {
                    Language.dispatchMessage(sender, "&cCan not find Vault or Economy plugin.", true);
                    return true;
                }
                Player player = PlayerHandler.getPlayerString(args[2]);
                if (player == null) {
                    String[] placeHolders = Language.newString();
                    placeHolders[1] = args[2];
                    Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                    return true;
                }
                ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                String item = args[1].toUpperCase();
                if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                    if (Material.getMaterial(item) != null) {
                        if (PermissionsHandler.hasPermission(sender, "barrierplus.buy." + item)) {
                            int amount = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Amount");
                            int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                            if (amount == 0) {
                                amount = itemMaxStackSize;
                            }
                            String priceType = ConfigHandler.getConfig("config.yml").getString("Buy.List." + item + ".Price-Type");
                            int price = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Price");
                            if (priceType == null) {
                                Language.dispatchMessage(sender, "&cCan not find the price type of &e" + item + "&c in config.");
                                return true;
                            }
                            if (PlayerHandler.getEnoughMoney(player, priceType, price)) {
                                int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                                int itemRemain = amount % itemMaxStackSize;
                                if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                    String[] placeHolders = Language.newString();
                                    double balance = PlayerHandler.takeTypeMoney(player, priceType, price);
                                    giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                    placeHolders[7] = item;
                                    placeHolders[6] = String.valueOf(amount);
                                    placeHolders[3] = priceType;
                                    placeHolders[4] = String.valueOf(price);
                                    placeHolders[5] = String.valueOf(balance);
                                    Language.sendLangMessage("Message.BarrierPlus.buySuccess", player, placeHolders);
                                    placeHolders[2] = player.getName();
                                    Language.sendLangMessage("Message.BarrierPlus.buyTargetSuccess", sender, placeHolders);
                                    return true;
                                }
                                Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
                                return true;
                            }
                            String[] placeHolders = Language.newString();
                            placeHolders[7] = item;
                            placeHolders[6] = String.valueOf(amount);
                            placeHolders[3] = priceType;
                            placeHolders[4] = String.valueOf(price);
                            placeHolders[5] = String.valueOf(PlayerHandler.getTypeBalance(player, priceType));
                            Language.sendLangMessage("Message.BarrierPlus.buyNotEnoughMoney", player, placeHolders);
                            placeHolders[2] = player.getName();
                            Language.sendLangMessage("Message.BarrierPlus.buyTargetNotEnoughMoney", sender, placeHolders);
                            return true;
                        } else {
                            Language.sendLangMessage("Message.BarrierPlus.buyNoPerm", sender);
                        }
                    }
                }
                Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("give")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                Language.sendLangMessage("Message.BarrierPlus.Commands.give", sender, false);
            } else if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                Language.sendLangMessage("Message.BarrierPlus.Commands.giveOther", sender, false);
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                if (!(sender instanceof ConsoleCommandSender)) {
                    ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                    String item = args[1].toUpperCase();
                    if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                        if (Material.getMaterial(item) != null) {
                            if (PermissionsHandler.hasPermission(sender, "barrierplus.give." + item)) {
                                Player player = (Player) sender;
                                int amount = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Amount");
                                int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                                if (amount == 0) {
                                    amount = itemMaxStackSize;
                                }
                                int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                                int itemRemain = amount % itemMaxStackSize;
                                if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                    giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                    String[] placeHolders = Language.newString();
                                    placeHolders[7] = item;
                                    placeHolders[6] = String.valueOf(amount);
                                    Language.sendLangMessage("Message.BarrierPlus.giveSuccess", sender, placeHolders);
                                    return true;
                                }
                                Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
                                return true;
                            } else {
                                Language.sendLangMessage("Message.BarrierPlus.giveNoPerm", sender);
                            }
                        }
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                    return true;
                }
                Language.dispatchMessage(sender, "&cYou are not a player &8- &c/barrierplus give <item> [player]", true);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give") && args[2].matches("-?[0-9]\\d*$")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                if (!(sender instanceof ConsoleCommandSender)) {
                    ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                    String item = args[1].toUpperCase();
                    if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                        if (Material.getMaterial(item) != null) {
                            if (PermissionsHandler.hasPermission(sender, "barrierplus.give." + item)) {
                                Player player = (Player) sender;
                                int amount = Integer.valueOf(args[2]);
                                int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                                int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                                int itemRemain = amount % itemMaxStackSize;
                                if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                    giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                    String[] placeHolders = Language.newString();
                                    placeHolders[7] = item;
                                    placeHolders[6] = String.valueOf(amount);
                                    Language.sendLangMessage("Message.BarrierPlus.giveSuccess", sender, placeHolders);
                                    return true;
                                }
                                Language.sendLangMessage("Message.BarrierPlus.inventoryFull", sender);
                                return true;
                            } else {
                                Language.sendLangMessage("Message.BarrierPlus.giveNoPerm", sender);
                            }
                        }
                    }
                    Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                    return true;
                }
                Language.dispatchMessage(sender, "&cYou are not a player &8- &c/barrierplus give <item> [amount] [player]", true);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                Player player = PlayerHandler.getPlayerString(args[2]);
                if (player == null) {
                    String[] placeHolders = Language.newString();
                    placeHolders[2] = args[2];
                    Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                    return true;
                }
                ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                String item = args[1].toUpperCase();
                if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                    if (Material.getMaterial(item) != null) {
                        if (PermissionsHandler.hasPermission(sender, "barrierplus.give." + item)) {
                            int amount = ConfigHandler.getConfig("config.yml").getInt("Buy.List." + item + ".Amount");
                            int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                            if (amount == 0) {
                                amount = itemMaxStackSize;
                            }
                            int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                            int itemRemain = amount % itemMaxStackSize;
                            if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                String[] placeHolders = Language.newString();
                                placeHolders[7] = item;
                                placeHolders[6] = String.valueOf(amount);
                                Language.sendLangMessage("Message.BarrierPlus.giveSuccess", player, placeHolders);
                                placeHolders[2] = player.getName();
                                Language.sendLangMessage("Message.BarrierPlus.giveTargetSuccess", sender, placeHolders);
                                return true;
                            }
                            Language.sendLangMessage("Message.BarrierPlus.inventoryFull", player);
                            String[] placeHolders = Language.newString();
                            placeHolders[2] = player.getName();
                            Language.sendLangMessage("Message.BarrierPlus.targetInventoryFull", sender, placeHolders);
                            return true;
                        } else {
                            Language.sendLangMessage("Message.BarrierPlus.giveNoPerm", sender);
                        }
                    }
                }
                Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give") && args[2].matches("-?[0-9]\\d*$")) {
            if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                Player player = PlayerHandler.getPlayerString(args[3]);
                if (player == null) {
                    String[] placeHolders = Language.newString();
                    placeHolders[1] = args[3];
                    Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                    return true;
                }
                ConfigurationSection buyBlockList = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.List");
                String item = args[1].toUpperCase();
                if (buyBlockList != null && buyBlockList.getKeys(false).contains(item)) {
                    if (Material.getMaterial(item) != null) {
                        if (PermissionsHandler.hasPermission(sender, "barrierplus.give." + item)) {
                            int amount = Integer.valueOf(args[2]);
                            int itemMaxStackSize = Material.getMaterial(item).getMaxStackSize();
                            int itemStacks = (int) Math.ceil(amount / itemMaxStackSize);
                            int itemRemain = amount % itemMaxStackSize;
                            if (getInventoryFull(player, item, itemMaxStackSize, itemStacks, itemRemain)) {
                                giveItem(player, item, itemMaxStackSize, itemStacks, itemRemain);
                                String[] placeHolders = Language.newString();
                                placeHolders[7] = item;
                                placeHolders[6] = String.valueOf(amount);
                                Language.sendLangMessage("Message.BarrierPlus.giveSuccess", player, placeHolders);
                                placeHolders[2] = player.getName();
                                Language.sendLangMessage("Message.BarrierPlus.giveTargetSuccess", sender, placeHolders);
                                return true;
                            }
                            Language.sendLangMessage("Message.BarrierPlus.inventoryFull", player);
                            String[] placeHolders = Language.newString();
                            placeHolders[2] = player.getName();
                            Language.sendLangMessage("Message.BarrierPlus.targetInventoryFull", sender, placeHolders);
                            return true;
                        } else {
                            Language.sendLangMessage("Message.BarrierPlus.giveNoPerm", sender);
                        }
                    }
                }
                Language.sendLangMessage("Message.BarrierPlus.noShopItem", sender);
                return true;
            } else {
                Language.sendLangMessage("Message.noPermission", sender);
            }
            return true;
        } else {
            Language.sendLangMessage("Message.unknownCommand", sender);
            return true;
        }
        return true;
    }

    private boolean getInventoryFull(Player player, String item, int itemMaxStackSize, int itemStacks, int itemRemain) {
        Material itemType = Material.getMaterial(item);
        int slotEmpty = 0;
        int slotRemain = 0;
        ItemStack slotItem;
        Material slotItemType;
        for (int i = 0; i <= 35; i++) {
            slotItem = player.getInventory().getItem(i);
            if (slotItem == null) {
                slotEmpty++;
            } else {
                slotItemType = slotItem.getType();
                if (slotItemType == itemType) {
                    slotRemain += itemMaxStackSize - slotItem.getAmount();
                    if (slotRemain >= itemMaxStackSize) {
                        slotRemain -= itemMaxStackSize;
                        slotEmpty++;
                    }
                }
            }
        }
        if (slotEmpty > itemStacks) {
            return true;
        } else return slotEmpty == itemStacks && slotRemain >= itemRemain;
    }

    private void giveItem(Player player, String item, int itemMaxStackSize, int itemStacks, int itemRemain) {
        Material itemType = Material.getMaterial(item);
        for (int i = 1; i <= itemStacks; i++) {
            player.getInventory().addItem(new ItemStack(itemType, itemMaxStackSize));
        }
        player.getInventory().addItem(new ItemStack(itemType, itemRemain));
    }
}