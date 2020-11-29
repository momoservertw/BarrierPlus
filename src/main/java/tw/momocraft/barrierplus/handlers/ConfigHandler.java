package tw.momocraft.barrierplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.listeners.*;
import tw.momocraft.barrierplus.utils.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigHandler {

    private static YamlConfiguration configYAML;
    private static DependAPI depends;
    private static UpdateHandler updater;
    private static ConfigPath configPath;

    public static void generateData(boolean reload) {
        genConfigFile("config.yml");
        setDepends(new DependAPI());
        sendUtilityDepends();
        setConfigPath(new ConfigPath());
        if (!reload) {
            setUpdater(new UpdateHandler());
        }
    }

    public static void registerEvents() {
        BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());

        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
        ServerHandler.sendFeatureMessage("Register-Event", "Destroy", "BlockBreak", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
        ServerHandler.sendFeatureMessage("Register-Event", "See & Destroy", "BlockClick", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockExplode(), BarrierPlus.getInstance());
        ServerHandler.sendFeatureMessage("Register-Event", "Destroy", "BlockExplode", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
        ServerHandler.sendFeatureMessage("Register-Event", "Place", "BlockPlace", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new EntityExplode(), BarrierPlus.getInstance());
        ServerHandler.sendFeatureMessage("Register-Event", "Destroy", "EntityExplode", "continue",
                new Throwable().getStackTrace()[0]);
        /*
        if (ConfigHandler.getDepends().ResidenceEnabled()) {
            if (ConfigHandler.getConfigPath().isSpawnResFlag()) {
                FlagPermissions.addFlag("spawnbypass");
            }
        }
         */
    }

    private static void sendUtilityDepends() {
        ServerHandler.sendConsoleMessage("&fHooked [ &e"
                + (getDepends().VaultEnabled() ? "Vault, " : "")
                + (getDepends().ResidenceEnabled() ? "Residence, " : "")
                + (getDepends().PlayerPointsEnabled() ? "PlayerPoints, " : "")
                + (getDepends().ItemJoinEnabled() ? "ItemJoin, " : "")
                + "&f]");
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
                ServerHandler.sendErrorMessage("&cCannot save " + fileName + " to disk!");
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
        String[] fileNameSlit = fileName.split("\\.(?=[^\\.]+$)");
        int configVersion = 0;
        File filePath = BarrierPlus.getInstance().getDataFolder();
        switch (fileName) {
            case "config.yml":
                configVersion = 6;
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
                    ServerHandler.sendConsoleMessage("&4The file \"" + fileName + "\" is out of date, generating a new one!");
                }
            }
        }
        getConfig(fileName).options().copyDefaults(false);
    }

    public static DependAPI getDepends() {
        return depends;
    }

    private static void setDepends(DependAPI depend) {
        depends = depend;
    }

    public static boolean isDebugging() {
        return ConfigHandler.getConfig("config.yml").getBoolean("Debugging");
    }

    public static UpdateHandler getUpdater() {
        return updater;
    }

    private static void setUpdater(UpdateHandler update) {
        updater = update;
    }

    public static ConfigPath getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(ConfigPath configPath) {
        ConfigHandler.configPath = configPath;
    }
}