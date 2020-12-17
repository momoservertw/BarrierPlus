package tw.momocraft.barrierplus.utils;

import tw.momocraft.coreplus.utils.locationutils.LocationMap;

import java.util.List;

public class DestroyMap {

    private boolean menuBreak;
    private boolean menuDrop;
    private boolean vanillaBreak;
    private boolean vanillaDrop;
    private boolean explodeBreak;
    private boolean explodeDrop;
    private List<LocationMap> locMaps;
    private List<LocationMap> preventLocMaps;

    public List<LocationMap> getLocMaps() {
        return locMaps;
    }

    public List<LocationMap> getPreventLocMaps() {
        return preventLocMaps;
    }

    public boolean isMenuBreak() {
        return menuBreak;
    }

    public boolean isMenuDrop() {
        return menuDrop;
    }

    public boolean isVanillaBreak() {
        return vanillaBreak;
    }

    public boolean isVanillaDrop() {
        return vanillaDrop;
    }

    public boolean isExplodeBreak() {
        return explodeBreak;
    }

    public boolean isExplodeDrop() {
        return explodeDrop;
    }

    public void setLocMaps(List<LocationMap> locMaps) {
        this.locMaps = locMaps;
    }

    public void setPreventLocMaps(List<LocationMap> preventLocMaps) {
        this.preventLocMaps = preventLocMaps;
    }

    public void setMenuBreak(boolean menuBreak) {
        this.menuBreak = menuBreak;
    }

    public void setMenuDrop(boolean menuDrop) {
        this.menuDrop = menuDrop;
    }

    public void setVanillaBreak(boolean vanillaBreak) {
        this.vanillaBreak = vanillaBreak;
    }

    public void setVanillaDrop(boolean vanillaDrop) {
        this.vanillaDrop = vanillaDrop;
    }

    public void setExplodeBreak(boolean explodeBreak) {
        this.explodeBreak = explodeBreak;
    }

    public void setExplodeDrop(boolean explodeDrop) {
        this.explodeDrop = explodeDrop;
    }
}
