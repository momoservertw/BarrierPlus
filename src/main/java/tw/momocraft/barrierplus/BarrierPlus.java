package tw.momocraft.barrierplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

public class BarrierPlus extends JavaPlugin {
    private static BarrierPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginPrefix(), "&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginPrefix(), "&fhas been Disabled.");
    }

    public static BarrierPlus getInstance() {
        return instance;
    }
}