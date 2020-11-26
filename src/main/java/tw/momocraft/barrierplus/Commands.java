package tw.momocraft.barrierplus;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.handlers.PlayerHandler;
import tw.momocraft.barrierplus.utils.Buy;
import tw.momocraft.barrierplus.utils.Language;

public class Commands implements CommandExecutor {

    private static Economy econ;

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        switch (args.length) {
            case 0:
                if (PermissionsHandler.hasPermission(sender, "barrierplus.use")) {
                    Language.sendLangMessage("Message.BarrierPlus.Commands.title", sender, false);
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.version")) {
                        Language.dispatchMessage(sender, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                    }
                    Language.sendLangMessage("Message.BarrierPlus.Commands.help", sender, false);
                } else {
                    Language.sendLangMessage("Message.noPermission", sender);
                }
            case 1:
                if (args[0].equalsIgnoreCase("help")) {
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
                        ConfigHandler.generateData(true);
                        Language.sendLangMessage("Message.configReload", sender);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.version")) {
                        Language.dispatchMessage(sender, "&d&lBarrierPlus &e&lv" + BarrierPlus.getInstance().getDescription().getVersion() + "&8 - &fby Momocraft");
                        ConfigHandler.getUpdater().checkUpdates(sender);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("buy")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy.other")) {
                        Language.sendLangMessage("Message.BarrierPlus.Commands.buyOther", sender, false);
                        Language.sendLangMessage("Message.BarrierPlus.Commands.buy", sender, false);
                    } else if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy")) {
                        Language.sendLangMessage("Message.BarrierPlus.Commands.buy", sender, false);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                        Language.sendLangMessage("Message.BarrierPlus.Commands.give", sender, false);
                        Language.sendLangMessage("Message.BarrierPlus.Commands.giveOther", sender, false);
                    } else if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                        Language.sendLangMessage("Message.BarrierPlus.Commands.giveOther", sender, false);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                }
            case 2:
                // /barrierplus buy <item>
                if (args[0].equalsIgnoreCase("buy")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy")) {
                        Buy.buyItem(sender, null, args[1].toUpperCase());
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                        Buy.giveItem(sender, null, args[1].toUpperCase(), 0);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                }
            case 3:
                // /barrierplus buy <item> [player]
                if (args[0].equalsIgnoreCase("buy")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.buy.other")) {
                        Player player = PlayerHandler.getPlayerString(args[2]);
                        if (player == null) {
                            String[] placeHolders = Language.newString();
                            placeHolders[1] = args[2];
                            Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                            return true;
                        }
                        Buy.buyItem(sender, player, args[1].toUpperCase());
                        return true;
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                    // /barrierplus give <item> [amount]
                } else if (args[0].equalsIgnoreCase("give") && args[2].matches("-?[0-9]\\d*$")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give")) {
                        Buy.giveItem(sender, null, args[1].toUpperCase(), Integer.parseInt(args[2]));
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                    // /barrierplus give <item> [player]
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                        Player player = PlayerHandler.getPlayerString(args[2]);
                        if (player == null) {
                            String[] placeHolders = Language.newString();
                            placeHolders[1] = args[2];
                            Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                            return true;
                        }
                        Buy.giveItem(sender, player, args[1].toUpperCase(), 0);
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                }
            case 4:
                // /barrierplus give <item> [amount] [player]
                if (args[0].equalsIgnoreCase("give") && args[2].matches("-?[0-9]\\d*$")) {
                    if (PermissionsHandler.hasPermission(sender, "barrierplus.command.give.other")) {
                        Player player = PlayerHandler.getPlayerString(args[2]);
                        if (player == null) {
                            String[] placeHolders = Language.newString();
                            placeHolders[1] = args[2];
                            Language.sendLangMessage("Message.targetNotFound", sender, placeHolders);
                            return true;
                        }
                        Buy.giveItem(sender, player, args[1].toUpperCase(), Integer.parseInt(args[2]));
                    } else {
                        Language.sendLangMessage("Message.noPermission", sender);
                    }
                    return true;
                }
            default:
                Language.sendLangMessage("Message.unknownCommand", sender);
                return true;
        }
    }
}