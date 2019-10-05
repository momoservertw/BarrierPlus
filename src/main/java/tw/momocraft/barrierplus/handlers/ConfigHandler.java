package tw.momocraft.barrierplus.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.listeners.BlockBreak;
import tw.momocraft.barrierplus.listeners.BlockClick;
import tw.momocraft.barrierplus.listeners.BlockPlace;
import tw.momocraft.barrierplus.utils.*;

import java.io.File;

public class ConfigHandler {

	private static YamlConfiguration configYAML;
	private static DependAPI depends;

	public static void generateData(File file) {
		configFile();
		setDepends(new DependAPI());
		sendUtilityDepends();
	}

	public static void registerEvents() {
		BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
		BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
	}

	public static FileConfiguration getConfig(String path) {
		File file = new File(BarrierPlus.getInstance().getDataFolder(), path);
		if (configYAML == null) {
			getConfigData(path);
		}
		return getPath(path, file, false);
	}

	public static FileConfiguration getConfigData(String path) {
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

	public static YamlConfiguration getPath(String path, File file, boolean saveData) {
		if (path.contains("config.yml")) {
			if (saveData) {
				configYAML = YamlConfiguration.loadConfiguration(file);
			}
			return configYAML;
		}
		return null;
	}

	public static void configFile() {
		getConfigData("config.yml");
		File File = new File(BarrierPlus.getInstance().getDataFolder(), "config.yml");
		if (File.exists() && getConfig("config.yml").getInt("config-Version") != 2) {
			if (BarrierPlus.getInstance().getResource("config.yml") != null) {
				String newGen = "config" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(BarrierPlus.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					File.renameTo(newFile);
					File configFile = new File(BarrierPlus.getInstance().getDataFolder(), "config.yml");
					configFile.delete();
					getConfigData("config.yml");
					ServerHandler.sendConsoleMessage("&aYour config.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		getConfig("config.yml").options().copyDefaults(false);
	}

	private static void sendUtilityDepends() {
		ServerHandler.sendConsoleMessage("&fUtilizing [ &e"
				+ (getDepends().getVault().vaultEnabled() ? "Vault, " : "")
				+ (getDepends().ResidenceEnabled() ? "Residence " : "")
				+ "&f]");
	}

	public static DependAPI getDepends() {
		return depends;
	}

	private static void setDepends(DependAPI depend) {
		depends = depend;
	}
}