package tw.momocraft.barrierplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.utils.ConfigPath;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigHandler {

    private static YamlConfiguration configYAML;
    private static ConfigPath configPath;

    public static void generateData(boolean reload) {
        genConfigFile("config.yml");
        UtilsHandler.setupFirst(reload);
        setConfigPath(new ConfigPath());
        if (!reload) {
            CorePlusAPI.getUpdate().check(getPlugin(), getPluginPrefix(), Bukkit.getConsoleSender(),
                    BarrierPlus.getInstance().getDescription().getName(),
                    BarrierPlus.getInstance().getDescription().getVersion(), true);
        }
    }

    public static FileConfiguration getConfig(String fileName) {
        File filePath = BarrierPlus.getInstance().getDataFolder();
        File file;
        switch (fileName) {
            case "config.yml":
                filePath = Bukkit.getWorldContainer();
                if (configYAML == null) {
                    getConfigData(filePath, fileName);
                }
                break;
            default:
                break;
        }
        file = new File(filePath, fileName);
        return getPath(fileName, file, false);
    }

    private static void getConfigData(File filePath, String fileName) {
        File file = new File(filePath, fileName);
        if (!(file).exists()) {
            try {
                BarrierPlus.getInstance().saveResource(fileName, false);
            } catch (Exception e) {
                CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPlugin(), "Cannot save " + fileName + " to disk!");
                return;
            }
        }
        getPath(fileName, file, true);
    }

    private static YamlConfiguration getPath(String fileName, File file, boolean saveData) {
        switch (fileName) {
            case "config.yml":
                if (saveData) {
                    configYAML = YamlConfiguration.loadConfiguration(file);
                }
                return configYAML;
        }
        return null;
    }

    private static void genConfigFile(String fileName) {
        String[] fileNameSlit = fileName.split("\\.(?=[^.]+$)");
        int configVersion = 0;
        File filePath = BarrierPlus.getInstance().getDataFolder();
        switch (fileName) {
            case "config.yml":
                configVersion = 7;
                break;
        }
        getConfigData(filePath, fileName);
        File file = new File(filePath, fileName);
        if (file.exists() && getConfig(fileName).getInt("Config-Version") != configVersion) {
            if (BarrierPlus.getInstance().getResource(fileName) != null) {
                LocalDateTime currentDate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                String currentTime = currentDate.format(formatter);
                String newGen = fileNameSlit[0] + " " + currentTime + "." + fileNameSlit[0];
                File newFile = new File(filePath, newGen);
                if (!newFile.exists()) {
                    file.renameTo(newFile);
                    File configFile = new File(filePath, fileName);
                    configFile.delete();
                    getConfigData(filePath, fileName);
                    CorePlusAPI.getMsg().sendConsoleMsg(getPrefix(), "&4The file \"" + fileName + "\" is out of date, generating a new one!");
                }
            }
        }
        getConfig(fileName).options().copyDefaults(false);
    }

    public static ConfigPath getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(ConfigPath configPaths) {
        configPath = configPaths;
    }

    public static String getPrefix() {
        return getConfig("config.yml").getString("Message.prefix");
    }

    public static String getPluginPrefix() {
        return "[" + BarrierPlus.getInstance().getDescription().getName() + "] ";
    }

    public static String getPlugin() {
        return BarrierPlus.getInstance().getDescription().getName();
    }

    public static boolean isDebug() {
        return ConfigHandler.getConfig("config.yml").getBoolean("Debugging");
    }
}