package tw.momocraft.barrierplus.utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ServerHandler;

public class VaultAPI {
    private Economy econ = null;
    private boolean isEnabled = false;

    public VaultAPI() {
        this.setVaultStatus(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null);
    }

    private void enableEconomy() {
        if (BarrierPlus.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
            if (!this.setupEconomy()) {
                ServerHandler.sendErrorMessage("There was an issue setting up Vault to work with BarrierPlus!");
                ServerHandler.sendErrorMessage("If this continues, please contact the plugin developer!");
            }
        }
    }

    private boolean setupEconomy() {
        if (BarrierPlus.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {  return false; }
        RegisteredServiceProvider<Economy> rsp = BarrierPlus.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.econ = rsp.getProvider();
        return this.econ != null;
    }

    public Economy getEconomy() {
        return this.econ;
    }

    public boolean vaultEnabled() {
        return this.isEnabled;
    }

    private void setVaultStatus(boolean bool) {
        if (bool) {
            this.enableEconomy();
        }
        this.isEnabled = bool;
    }

    public double getBalance(Player player) {
        return this.econ.getBalance(player);
    }

    public EconomyResponse withdrawBalance(Player player, int cost) {
        return this.econ.withdrawPlayer(player, cost);
    }
}