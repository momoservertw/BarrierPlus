package tw.momocraft.barrierplus.handlers;

import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.listeners.*;
import tw.momocraft.barrierplus.TabComplete;
import tw.momocraft.coreplus.api.CorePlusAPI;


public class RegisterHandler {

    public static void registerEvents() {
        BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());
        BarrierPlus.getInstance().getCommand("barrierplus").setTabCompleter(new TabComplete());

        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "Spawner", "SpawnerSpawn", "continue",
                new Throwable().getStackTrace()[0]);
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "Destroy", "BlockBreak", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "See & Destroy", "BlockClick", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockExplode(), BarrierPlus.getInstance());
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "Destroy", "BlockExplode", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "Place", "BlockPlace", "continue",
                new Throwable().getStackTrace()[0]);
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new EntityExplode(), BarrierPlus.getInstance());
        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.isDebugging(), ConfigHandler.getPluginPrefix(), "Register-Event", "Destroy", "EntityExplode", "continue",
                new Throwable().getStackTrace()[0]);
    }
}
