package tw.momocraft.barrierplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;

public class BarrierPlus extends JavaPlugin {
    private static BarrierPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        ConfigHandler.registerEvents();
        ServerHandler.sendConsoleMessage("&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        ServerHandler.sendConsoleMessage("&fhas been Disabled.");
    }

    public static BarrierPlus getInstance() {
        return instance;
    }
}