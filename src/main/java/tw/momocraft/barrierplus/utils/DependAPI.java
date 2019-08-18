package tw.momocraft.barrierplus.utils;

import org.bukkit.Bukkit;

public class DependAPI {
	private boolean Residence = false;
	private VaultAPI vault;
	
	public DependAPI() {
		this.setResidenceStatus(Bukkit.getServer().getPluginManager().getPlugin("Residence") != null);
		this.setVault();
	}

	public boolean ResidenceEnabled() {
		return this.Residence;
	}

	public void setResidenceStatus(boolean bool) {
		this.Residence = bool;
	}

	public VaultAPI getVault() {
		return this.vault;
	}

	private void setVault() {
		this.vault = new VaultAPI();
	}
}
