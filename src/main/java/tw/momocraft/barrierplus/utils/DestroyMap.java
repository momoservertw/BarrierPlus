package tw.momocraft.barrierplus.utils;

import tw.momocraft.barrierplus.utils.locationutils.LocationMap;

import java.util.List;

public class DestroyMap {

    private String menuBreak;
    private String menuDrop;
    private String vanillaBreak;
    private String vanillaDrop;
    private String explodeBreak;
    private String explodeDrop;
    private List<LocationMap> locMaps;
    private List<LocationMap> preventLocMaps;

    public List<LocationMap> getLocMaps() {
        return locMaps;
    }

    public List<LocationMap> getPreventLocMaps() {
        return preventLocMaps;
    }

    public String getExplodeBreak() {
        return explodeBreak;
    }

    public String getExplodeDrop() {
        return explodeDrop;
    }

    public String getMenuBreak() {
        return menuBreak;
    }

    public String getMenuDrop() {
        return menuDrop;
    }

    public String getVanillaBreak() {
        return vanillaBreak;
    }

    public String getVanillaDrop() {
        return vanillaDrop;
    }

    public void setLocMaps(List<LocationMap> locMaps) {
        this.locMaps = locMaps;
    }

    public void setPreventLocMaps(List<LocationMap> preventLocMaps) {
        this.preventLocMaps = preventLocMaps;
    }

    public void setExplodeBreak(String explodeBreak) {
        this.explodeBreak = explodeBreak;
    }

    public void setExplodeDrop(String explodeDrop) {
        this.explodeDrop = explodeDrop;
    }

    public void setMenuBreak(String menuBreak) {
        this.menuBreak = menuBreak;
    }

    public void setMenuDrop(String menuDrop) {
        this.menuDrop = menuDrop;
    }

    public void setVanillaBreak(String vanillaBreak) {
        this.vanillaBreak = vanillaBreak;
    }

    public void setVanillaDrop(String vanillaDrop) {
        this.vanillaDrop = vanillaDrop;
    }
}
