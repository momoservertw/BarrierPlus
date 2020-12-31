package tw.momocraft.barrierplus;

import org.bukkit.plugin.java.JavaPlugin;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.RegisterHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

public class BarrierPlus extends JavaPlugin {
    private static BarrierPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.generateData(false);
        RegisterHandler.registerEvents();
        CorePlusAPI.getLangManager().sendConsoleMsg(ConfigHandler.getPlugin(), "&fhas been Enabled.");
    }

    @Override
    public void onDisable() {
        CorePlusAPI.getLangManager().sendConsoleMsg(ConfigHandler.getPlugin(), "&fhas been Disabled.");
    }

    public static BarrierPlus getInstance() {
        return instance;
    }
}