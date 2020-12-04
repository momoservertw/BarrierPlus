package tw.momocraft.barrierplus.handlers;

import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.listeners.*;
import tw.momocraft.barrierplus.utils.TabComplete;


public class RegisterHandler {

    public static void registerEvents() {
        BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());
        BarrierPlus.getInstance().getCommand("barrierplus").setTabCompleter(new TabComplete());

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
        if (ConfigHandler.getDepends().MyPetEnabled()) {
            LotteryPlus.getInstance().getServer().getPluginManager().registerEvents(new MyPet(), ServerPlus.getInstance());
            ServerHandler.sendFeatureMessage("Register-Event", "MyPet", "MyPet", "continue",
                    new Throwable().getStackTrace()[0]);
        }
         */
    }
}
