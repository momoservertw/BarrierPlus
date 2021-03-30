package tw.momocraft.barrierplus.handlers;

import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.Commands;
import tw.momocraft.barrierplus.TabComplete;
import tw.momocraft.barrierplus.listeners.*;

public class DependHandler {

    public DependHandler() {
        registerEvents();
    }

    private void registerEvents() {
        BarrierPlus.getInstance().getCommand("barrierplus").setExecutor(new Commands());
        BarrierPlus.getInstance().getCommand("barrierplus").setTabCompleter(new TabComplete());

        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockBreak(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockClick(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockExplode(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new BlockPlace(), BarrierPlus.getInstance());
        BarrierPlus.getInstance().getServer().getPluginManager().registerEvents(new EntityExplode(), BarrierPlus.getInstance());
    }
}
