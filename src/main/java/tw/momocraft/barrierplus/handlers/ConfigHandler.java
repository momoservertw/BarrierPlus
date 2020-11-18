package tw.momocraft.barrierplus.handlers;

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

	public static void generateData() {
		configFile();
		setDepends(new DependAPI());
		sendUtilityDepends();
		setUpdater(new UpdateHandler());
	}

	public static void registerEvents() {
		BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockExplode(), BarrierPlus.getInstance());
		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new EntityExplode(), BarrierPlus.getInstance());

		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockDropItem(), BarrierPlus.getInstance());
		ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
				new Throwable().getStackTrace()[0]);
	}

	public static FileConfiguration getConfig(String path) {
		File file = new File(BarrierPlus.getInstance().getDataFolder(), path);
		if (configYAML == null) {
			getConfigData(path);
		}
		return getPath(path, file, false);
	}

	private static FileConfiguration getConfigData(String path) {
		File file = new File(BarrierPlus.getInstance().getDataFolder(), path);
		if (!(file).exists()) {
			try {
				BarrierPlus.getInstance().saveResource(path, false);
			} catch (Exception e) {
				BarrierPlus.getInstance().getLogger().warning("Cannot save " + path + " to disk!");
				return null;
			}
		}
		return getPath(path, file, true);
	}

	private static YamlConfiguration getPath(String path, File file, boolean saveData) {
		if (path.contains("config.yml")) {
			if (saveData) {
				configYAML = YamlConfiguration.loadConfiguration(file);
			}
			return configYAML;
		}
		return null;
	}

	private static void configFile() {
		getConfigData("config.yml");
		File File = new File(BarrierPlus.getInstance().getDataFolder(), "config.yml");
		if (File.exists() && getConfig("config.yml").getInt("Config-Version") != 5) {
			if (BarrierPlus.getInstance().getResource("config.yml") != null) {
				LocalDateTime currentDate = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
				String currentTime = currentDate.format(formatter);
				String newGen = "config " + currentTime + ".yml";
				File newFile = new File(BarrierPlus.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					File.renameTo(newFile);
					File configFile = new File(BarrierPlus.getInstance().getDataFolder(), "config.yml");
					configFile.delete();
					getConfigData("config.yml");
					ServerHandler.sendConsoleMessage("&e*            *            *");
					ServerHandler.sendConsoleMessage("&e *            *            *");
					ServerHandler.sendConsoleMessage("&e  *            *            *");
					ServerHandler.sendConsoleMessage("&cYour config.yml is out of date, generating a new one!");
					ServerHandler.sendConsoleMessage("&e    *            *            *");
					ServerHandler.sendConsoleMessage("&e     *            *            *");
					ServerHandler.sendConsoleMessage("&e      *            *            *");
				}
			}
		}
		getConfig("config.yml").options().copyDefaults(false);
	}

	private static void sendUtilityDepends() {
		ServerHandler.sendConsoleMessage("&fHooked [ &e"
				+ (getDepends().getVault().vaultEnabled() ? "Vault, " : "")
				+ (getDepends().ResidenceEnabled() ? "Residence, " : "")
				+ (getDepends().PlayerPointsEnabled() ? "PlayerPoints, " : "")
				+ "&f]");
	}

	public static DependAPI getDepends() {
		return depends;
	}

	private static void setDepends(DependAPI depend) {
		depends = depend;
	}

	public static boolean getDebugging() {
		return ConfigHandler.getConfig("config.yml").getBoolean("Debugging");
	}

	public static UpdateHandler getUpdater() {
		return updater;
	}

	private static void setUpdater(UpdateHandler update) {
		updater = update;
	}
}