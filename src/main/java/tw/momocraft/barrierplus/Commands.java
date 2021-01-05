package tw.momocraft.barrierplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.Buy;
import tw.momocraft.coreplus.CorePlus;
import tw.momocraft.coreplus.api.CorePlusAPI;

public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        switch (args.length) {
            case 0:
                if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.use")) {
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgTitle(), sender);
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&f " + BarrierPlus.getInstance().getDescription().getName()
                            + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgHelp(), sender);
                    CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                } else {
                    CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                }
                return true;
            case 1:
                if (args[0].equalsIgnoreCase("help")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.use")) {
                        CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgTitle(), sender);
                        CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&f " + BarrierPlus.getInstance().getDescription().getName()
                                + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgHelp(), sender);
                        if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.reload")) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgReload(), sender);
                        }
                        if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.version")) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgVersion(), sender);
                        }
                        if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy")) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBuy(), sender);
                        }
                        if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy.other")) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBuyOther(), sender);
                        }
                        CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "");
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.reload")) {
                        ConfigHandler.generateData(true);
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.configReload", sender);
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.version")) {
                        CorePlusAPI.getLangManager().sendMsg(ConfigHandler.getPrefix(), sender, "&f " + BarrierPlus.getInstance().getDescription().getName()
                                + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                        CorePlusAPI.getUpdateManager().check(ConfigHandler.getPrefix(), sender,
                                CorePlus.getInstance().getName(), CorePlus.getInstance().getDescription().getVersion(), true);
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("buy")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy.other")) {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBuyOther(), sender);
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBuy(), sender);
                    } else if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy")) {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), ConfigHandler.getConfigPath().getMsgBuy(), sender);
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                }
                break;
            case 2:
                // /barrierplus buy <item>
                if (args[0].equalsIgnoreCase("buy")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy")) {
                        if (!ConfigHandler.getConfigPath().isBuy()) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.featureDisabled", sender);
                            return true;
                        }
                        Buy.buyItem(sender, null, args[1].toUpperCase());
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                }
                break;
            case 3:
                // /barrierplus buy <item> [player]
                if (args[0].equalsIgnoreCase("buy")) {
                    if (CorePlusAPI.getPlayerManager().hasPermission(sender, "barrierplus.command.buy.other")) {
                        if (!ConfigHandler.getConfigPath().isBuy()) {
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.featureDisabled", sender);
                            return true;
                        }
                        Player player = CorePlusAPI.getPlayerManager().getPlayerString(args[2]);
                        if (player == null) {
                            String[] placeHolders = CorePlusAPI.getLangManager().newString();
                            placeHolders[1] = args[2]; // %targetplayer%
                            CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.targetNotFound", sender, placeHolders);
                            return true;
                        }
                        Buy.buyItem(sender, player, args[1].toUpperCase());
                        return true;
                    } else {
                        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
                    }
                    return true;
                }
                break;
        }
        CorePlusAPI.getLangManager().sendLangMsg(ConfigHandler.getPrefix(), "Message.unknownCommand", sender);
        return true;
    }
}