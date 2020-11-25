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
		if (ConfigHandler.getDebugging()) {
			String prefix = "&7[&dBarrierPlus_Debug&7] ";
			message = prefix + message;
			message = ChatColor.translateAlternateColorCodes('&', message);
			BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
		}
	}

	public static void sendDebugMessage(String message, boolean check) {
		if (!check) {
			if (!ConfigHandler.getDebugging()) {
				return;
			}
		}
		String prefix = "&7[&dBarrierPlus_Debug&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message);
		BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
	}

	public static void sendErrorMessage(String message) {
		String prefix = "&7[&cBarrierPlus_ERROR&7]&c ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message);
		BarrierPlus.getInstance().getServer().getConsoleSender().sendMessage(message);
	}

	public static void sendPlayerMessage(Player player, String message) {
		String prefix = "&7[&dBarrierPlus&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (message.contains("blankmessage")) {
			message = "";
		}
		player.sendMessage(message);
	}

	public static void sendMessage(CommandSender sender, String message) {
		String prefix = "&7[&dBarrierPlus&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message);
		sender.sendMessage(message);
	}

	public static void sendDebugTrace(Exception e) {
		if (ConfigHandler.getDebugging()) {
			e.printStackTrace();
		}
	}

	public static void sendFeatureMessage(String feature, String target, String check, String action, String detail, StackTraceElement ste) {
		if (!ConfigHandler.getDebugging()) {
			return;
		}
		switch (action) {
			case "cancel":
			case "remove":
			case "kill":
			case "damage":
			case "fail":
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &c" + action + "&8, &7" + detail
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
			case "continue":
			case "bypass":
			case "change":
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &e" + action + "&8, &7" + detail
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
			case "return":
			case "success":
			default:
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &a" + action + "&8, &7" + detail
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
		}
	}

	public static void sendFeatureMessage(String feature, String target, String check, String action, StackTraceElement ste) {
		if (!ConfigHandler.getDebugging()) {
			return;
		}
		switch (action) {
			case "cancel":
			case "remove":
			case "kill":
			case "damage":
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &c" + action
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
			case "continue":
			case "bypass":
			case "change":
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &e" + action
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
			case "return":
			case "success":
			default:
				ServerHandler.sendDebugMessage("&f" + feature + "&8 - &f" + target + "&8 : &f" + check + "&8, &a" + action
						+ " &8(" + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + ")", true);
				break;
		}
	}

	public static void debugMessage(String feature, String target, String check, String action, String detail) {
		if (ConfigHandler.getDebugging()) {
			if (action.equals("return")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&a" + action + "&8, " + detail);
			} else if (action.equals("cancel")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
			} else if (action.equals("continue")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
			} else if (action.equals("bypass")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
			} else if (action.equals("remove")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action + "&8, " + detail);
			} else if (action.equals("change")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action + "&8, " + detail);
			}
		}
	}

	public static void debugMessage(String feature, String target, String check, String action) {
		if (ConfigHandler.getDebugging()) {
			if (action.equals("return")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&a" + action);
			} else if (action.equals("cancel")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
			} else if (action.equals("continue")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
			} else if (action.equals("bypass")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
			} else if (action.equals("remove")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&c" + action);
			} else if (action.equals("change")) {
				ServerHandler.sendDebugMessage("&8" + feature + " - &f" + target + "&8 : &7" + check + "&8, " + "&e" + action);
			}
		}
	}
}
