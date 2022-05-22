package tw.momocraft.barrierplus.utils;

import java.util.List;

public class DestroyMap {

    private String groupName;
    private boolean menuBreak;
    private boolean menuDrop;
    private boolean vanillaBreak;
    private boolean vanillaDrop;
    private boolean explodeBreak;
    private boolean explodeDrop;
    private List<String> locationList;
    private List<String> conditions;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isMenuBreak() {
        return menuBreak;
    }

    public void setMenuBreak(boolean menuBreak) {
        this.menuBreak = menuBreak;
    }

    public boolean isMenuDrop() {
        return menuDrop;
    }

    public void setMenuDrop(boolean menuDrop) {
        this.menuDrop = menuDrop;
    }

    public boolean isVanillaBreak() {
        return vanillaBreak;
    }

    public void setVanillaBreak(boolean vanillaBreak) {
        this.vanillaBreak = vanillaBreak;
    }

    public boolean isVanillaDrop() {
        return vanillaDrop;
    }

    public void setVanillaDrop(boolean vanillaDrop) {
        this.vanillaDrop = vanillaDrop;
    }

    public boolean isExplodeBreak() {
        return explodeBreak;
    }

    public void setExplodeBreak(boolean explodeBreak) {
        this.explodeBreak = explodeBreak;
    }

    public boolean isExplodeDrop() {
        return explodeDrop;
    }

    public void setExplodeDrop(boolean explodeDrop) {
        this.explodeDrop = explodeDrop;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<String> locationList) {
        this.locationList = locationList;
    }
}
