package tw.momocraft.barrierplus.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;

import java.util.Arrays;
import java.util.Map;

public class Language {
    private static final Lang langType = Lang.ENGLISH;

    public static void dispatchMessage(CommandSender sender, String langMessage, boolean hasPrefix) {
        if (hasPrefix) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            langMessage = Utils.translateLayout(langMessage, player);
            String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Message.prefix"), player);
            if (prefix == null) {
                prefix = "";
            } else {
                prefix += "";
            }
            langMessage = prefix + langMessage;
            sender.sendMessage(langMessage);
        } else {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            langMessage = Utils.translateLayout(langMessage, player);
            sender.sendMessage(langMessage);
        }
    }

    public static void dispatchMessage(CommandSender sender, String langMessage) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        langMessage = Utils.translateLayout(langMessage, player);
        sender.sendMessage(langMessage);
    }

    public static void sendLangMessage(String nodeLocation, CommandSender sender, String... placeHolder) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        String langMessage = ConfigHandler.getConfig(langType.nodeLocation()).getString(nodeLocation);
        String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Message.prefix"), player);
        if (prefix == null) {
            prefix = "";
        } else {
            prefix += "";
        }
        if (langMessage != null && !langMessage.isEmpty()) {
            langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
            langMessage = Utils.translateLayout(langMessage, player);
            String[] langLines = langMessage.split(" /n ");
            for (String langLine : langLines) {
                String langStrip = prefix + langLine;
                if (isConsoleMessage(nodeLocation)) {
                    ServerHandler.sendConsoleMessage(langLine);
                } else {
                    sender.sendMessage(langStrip);
                }
            }
        }
    }

    public static void sendLangMessage(String nodeLocation, CommandSender sender, boolean hasPrefix, String... placeHolder) {
        if (hasPrefix) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            String langMessage = ConfigHandler.getConfig(langType.nodeLocation()).getString(nodeLocation);
            String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Message.prefix"), player);
            if (prefix == null) {
                prefix = "";
            } else {
                prefix += "";
            }
            if (langMessage != null && !langMessage.isEmpty()) {
                langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
                langMessage = Utils.translateLayout(langMessage, player);
                String[] langLines = langMessage.split(" /n ");
                for (String langLine : langLines) {
                    String langStrip = prefix + langLine;
                    sender.sendMessage(langStrip);
                }
            }
        } else {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            String langMessage = ConfigHandler.getConfig(langType.nodeLocation()).getString(nodeLocation);
            if (langMessage != null && !langMessage.isEmpty()) {
                langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
                langMessage = Utils.translateLayout(langMessage, player);
                String[] langLines = langMessage.split(" /n ");
                for (String langLine : langLines) {
                    sender.sendMessage(langLine);
                }
            }
        }
    }

    private static String[] initializeRows(String... placeHolder) {
        if (placeHolder == null || placeHolder.length != newString().length) {
            String[] langHolder = Language.newString();
            Arrays.fill(langHolder, "null");
            return langHolder;
        } else {
            for (int i = 0; i < placeHolder.length; i++) {
                if (placeHolder[i] == null) {
                    placeHolder[i] = "null";
                }
            }
            return placeHolder;
        }
    }

    private static String translateLangHolders(String langMessage, String... langHolder) {
        Map<String, String> translateMap = ConfigHandler.getConfigPath().getTranslateMap();
        if (translateMap.containsKey(langHolder[3])) {
            langHolder[3] = translateMap.get(langHolder[3]);
        }
        if (translateMap.containsKey(langHolder[7])) {
            langHolder[7] = translateMap.get(langHolder[7]);
        }
        return langMessage
                .replace("%command%", langHolder[0])
                .replace("%player%", langHolder[1])
                .replace("%targetplayer%", langHolder[2])
                .replace("%pricetype%", langHolder[3])
                .replace("%price%", langHolder[4])
                .replace("%balance%", langHolder[5])
                .replace("%amount%", langHolder[6])
                .replace("%item%", langHolder[7]);
    }

    public static String[] newString() {
        return new String[14];
    }


    private enum Lang {
        ENGLISH("config.yml", 1);

        Lang(final String nodeLocation, final int i) {
            this.nodeLocation = nodeLocation;
        }

        private final String nodeLocation;

        private String nodeLocation() {
            return nodeLocation;
        }
    }

    private static boolean isConsoleMessage(String nodeLocation) {
        return false;
    }
}