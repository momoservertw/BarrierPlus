package tw.momocraft.barrierplus.utils;

import java.util.List;

public class SeeMap {
    private String groupName;
    private String particle;
    private boolean creative;

    private List<String> locationList;
    private List<String> conditions;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getParticle() {
        return particle;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public boolean isCreative() {
        return creative;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    public List<String> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<String> locationList) {
        this.locationList = locationList;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}
