package tw.momocraft.barrierplus.utils;

import java.util.List;

public class DestroyMap {

    private boolean menuBreak;
    private boolean menuDrop;
    private boolean vanillaBreak;
    private boolean vanillaDrop;
    private boolean explodeBreak;
    private boolean explodeDrop;
    private List<String> locList;
    private List<String> preventLocList;

    public List<String> getLocList() {
        return locList;
    }

    public List<String> getPreventLocList() {
        return preventLocList;
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

    public void setLocList(List<String> locList) {
        this.locList = locList;
    }

    public void setPreventLocList(List<String> preventLocList) {
        this.preventLocList = preventLocList;
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
