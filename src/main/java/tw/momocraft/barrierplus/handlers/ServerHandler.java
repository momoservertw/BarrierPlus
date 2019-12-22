package tw.momocraft.barrierplus.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.BarrierPlus;

public class ServerHandler {
		public static void sendConsoleMessage(String message) {
		String prefix = "&7[&dBarrierPlus&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
	}

	public static void sendDebugMessage(String message) {
		String prefix = "&7[&dBarrierPlus_Debug&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
	}

	public static void sendErrorMessage(String message) {
		String prefix = "&7[&cBarrierPlus_ERROR&7]&c ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
	}
	
	public static void sendPlayerMessage(Player player, String message) {
		String prefix = "&7[&dBarrierPlus&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
			if (message.contains("blankmessage")) {
				message = "";
		}
		player.sendMessage(message);
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		String prefix = "&7[&dBarrierPlus&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		sender.sendMessage(message);
	}
}
