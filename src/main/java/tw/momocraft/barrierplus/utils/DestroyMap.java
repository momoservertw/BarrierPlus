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
    private List<LocationMap> locationMaps;

    public List<LocationMap> getLocationMaps() {
        return locationMaps;
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

    public void setLocationMaps(List<LocationMap> locationMaps) {
        this.locationMaps = locationMaps;
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
