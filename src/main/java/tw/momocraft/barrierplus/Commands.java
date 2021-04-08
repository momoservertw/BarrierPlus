package tw.momocraft.barrierplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        int length = args.length;
        if (length == 0) {
            if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.use")) {
                CorePlusAPI.getMsg().sendMsg("", sender, "");
                CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgTitle(), sender);
                CorePlusAPI.getMsg().sendMsg("", sender,
                        "&f " + BarrierPlus.getInstance().getDescription().getName()
                                + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgHelp(), sender);
                CorePlusAPI.getMsg().sendMsg("", sender, "");
            } else {
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(), "Message.noPermission", sender);
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.use")) {
                    CorePlusAPI.getMsg().sendMsg("", sender, "");
                    CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgTitle(), sender);
                    CorePlusAPI.getMsg().sendMsg("", sender,
                            "&f " + BarrierPlus.getInstance().getDescription().getName()
                                    + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgHelp(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.command.reload"))
                        CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgReload(), sender);
                    if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.command.version"))
                        CorePlusAPI.getMsg().sendLangMsg("", ConfigHandler.getConfigPath().getMsgVersion(), sender);
                    CorePlusAPI.getMsg().sendMsg("", sender, "");
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "reload":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.command.reload")) {
                    ConfigHandler.generateData(true);
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.configReload", sender);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
            case "version":
                if (CorePlusAPI.getPlayer().hasPerm(sender, "regionplus.command.version")) {
                    CorePlusAPI.getMsg().sendMsg("", sender,
                            "&f " + BarrierPlus.getInstance().getDescription().getName()
                                    + " &ev" + BarrierPlus.getInstance().getDescription().getVersion() + "  &8by Momocraft");
                    CorePlusAPI.getUpdate().check(ConfigHandler.getPlugin(), "", sender,
                            BarrierPlus.getInstance().getName(), BarrierPlus.getInstance().getDescription().getVersion(), true);
                } else {
                    CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                            "Message.noPermission", sender);
                }
                return true;
        }
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPrefix(),
                "Message.unknownCommand", sender);
        return true;
    }
}

