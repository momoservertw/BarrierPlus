package tw.momocraft.barrierplus.utils;

import org.bukkit.Bukkit;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.handlers.ServerHandler;

public class DependAPI {
    private VaultAPI vaultApi;
    private boolean Vault = false;
    private boolean Residence = false;
    private boolean PlayerPoints = false;
    private boolean PlaceHolderAPI = false;
    private boolean ItemJoin = false;

    public DependAPI() {
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Vault")) {
            this.setVaultStatus(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null);
            if (Vault) {
                setVaultApi();
            }
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
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.ItemJoin")) {
            this.setItemJoinStatus(Bukkit.getServer().getPluginManager().getPlugin("ItemJoin") != null);
        }

        sendUtilityDepends();
    }

    private void sendUtilityDepends() {
        ServerHandler.sendConsoleMessage("&fHooked [ &e"
                + (VaultEnabled() ? "Vault, " : "")
                + (ResidenceEnabled() ? "Residence, " : "")
                + (PlayerPointsEnabled() ? "PlayerPoints, " : "")
                + (ItemJoinEnabled() ? "ItemJoin, " : "")
                + "&f]");
    }

    public boolean VaultEnabled() {
        return this.Vault;
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

    public boolean ItemJoinEnabled() {
        return this.ItemJoin;
    }


    public void setVaultStatus(boolean bool) {
        this.Vault = bool;
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

    public void setItemJoinStatus(boolean bool) {
        this.ItemJoin = bool;
    }


    public VaultAPI getVaultApi() {
        return this.vaultApi;
    }

    private void setVaultApi() {
        vaultApi = new VaultAPI();
    }

}
