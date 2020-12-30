package tw.momocraft.barrierplus.utils;

import java.util.List;

public class SeeMap {
    private String creative;
    private String particle;

    private List<String> locList;

    public String getParticle() {
        return particle;
    }

    public List<String> getLocList() {
        return locList;
    }

    public String getCreative() {
        return creative;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public void setCreative(String creative) {
        this.creative = creative;
    }

    public void setLocList(List<String> locList) {
        this.locList = locList;
    }
}
