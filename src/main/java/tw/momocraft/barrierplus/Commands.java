package tw.momocraft.barrierplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.momocraft.barrierplus.handlers.PermissionsHandler;
import tw.momocraft.barrierplus.utils.Language;

public class Commands implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
        if (PermissionsHandler.hasPermission(sender, "barrierplus.admin")) {
            Language.dispatchMessage(sender, "&aBarrierPlus v" + BarrierPlus.getInstance().getDescription().getVersion() + "&d by Momocraft");
        } else {
            Language.sendLangMessage("Message.Commands.noPermission", sender);
        }
        return true;
    }
}