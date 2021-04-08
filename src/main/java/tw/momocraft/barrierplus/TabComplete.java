package tw.momocraft.barrierplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import tw.momocraft.coreplus.handlers.UtilsHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        int length = args.length;
        if (length == 1) {
            if (UtilsHandler.getPlayer().hasPerm(sender, "barrierplus.use"))
                commands.add("help");
            if (UtilsHandler.getPlayer().hasPerm(sender, "barrierplus.command.reload"))
                commands.add("reload");
            if (UtilsHandler.getPlayer().hasPerm(sender, "barrierplus.command.version"))
                commands.add("version");
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}