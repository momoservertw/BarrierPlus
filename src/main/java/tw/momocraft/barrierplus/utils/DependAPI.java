package tw.momocraft.barrierplus.utils;

import org.bukkit.Bukkit;

public class DependAPI {
    private boolean Residence = false;
    private boolean PlayerPoints = false;
    private VaultAPI vault;

    public DependAPI() {
        this.setResidenceStatus(Bukkit.getServer().getPluginManager().getPlugin("Residence") != null);
        this.setPlayerPointsStatus(Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints") != null);
        this.setVault();
    }

    public boolean ResidenceEnabled() {
        return this.Residence;
    }

    public boolean PlayerPointsEnabled() {
        return this.PlayerPoints;
    }

    public void setResidenceStatus(boolean bool) {
        this.Residence = bool;
    }

    public void setPlayerPointsStatus(boolean bool) {
        this.PlayerPoints = bool;
    }

    public VaultAPI getVault() {
        return this.vault;
    }

    private void setVault() {
        this.vault = new VaultAPI();
    }
}
