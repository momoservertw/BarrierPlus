package tw.momocraft.barrierplus.handlers;

import org.bukkit.Bukkit;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.TabComplete;
import tw.momocraft.barrierplus.listeners.*;
import tw.momocraft.coreplus.api.CorePlusAPI;

public class DependHandler {

    public void setup(boolean reload) {
        if (!reload) {
            registerEvents();
            checkUpdate();
        }
    }

    public void checkUpdate() {
        if (!ConfigHandler.isCheckUpdates())
            return;
        CorePlusAPI.getUpdate().check(ConfigHandler.getPluginName(),
                ConfigHandler.getPluginPrefix(), Bukkit.getConsoleSender(),
                BarrierPlus.getInstance().getDescription().getName(),
                BarrierPlus.getInstance().getDescription().getVersion(), true);
    }

    private void registerEvents() {
        BarrierPlus.getInstance().getCommand("HotkeyPlus").setExecutor(new Commands());
        BarrierPlus.getInstance().getCommand("HotkeyPlus").setTabCompleter(new TabComplete());

        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockExplode(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new EntityExplode(), BarrierPlus.getInstance());
    }
}
