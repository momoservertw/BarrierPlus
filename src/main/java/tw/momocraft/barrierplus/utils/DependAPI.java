package tw.momocraft.barrierplus.utils;

import org.bukkit.Bukkit;
import tw.momocraft.barrierplus.handlers.ConfigHandler;

public class DependAPI {
    private boolean Residence = false;
    private boolean PlayerPoints = false;
    private boolean PlaceHolderAPI = false;
    private VaultAPI vault;

    public DependAPI() {
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Vault")) {
            this.setVault();
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.PlaceHolderAPI")) {
            this.setPlaceHolderStatus(Bukkit.getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Residence")) {
            this.setResidenceStatus(Bukkit.getServer().getPluginManager().getPlugin("Residence") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.PlayerPoints")) {
            this.setPlayerPointsStatus(Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints") != null);
        }
    }

    public boolean ResidenceEnabled() {
        return this.Residence;
    }

    public boolean PlayerPointsEnabled() {
        return this.PlayerPoints;
    }

    public boolean PlaceHolderAPIEnabled() {
        return this.PlaceHolderAPI;
    }

    public void setResidenceStatus(boolean bool) {
        this.Residence = bool;
    }

    public void setPlayerPointsStatus(boolean bool) {
        this.PlayerPoints = bool;
    }

    public void setPlaceHolderStatus(boolean bool) {
        this.PlaceHolderAPI = bool;
    }

    public VaultAPI getVault() {
        return this.vault;
    }

    private void setVault() {
        this.vault = new VaultAPI();
    }
}
