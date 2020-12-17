package tw.momocraft.barrierplus.utils;

import tw.momocraft.coreplus.utils.locationutils.LocationMap;

import java.util.List;

public class SeeMap {
    private String creative;
    private String particle;

    private List<LocationMap> locMaps;

    public String getParticle() {
        return particle;
    }

    public List<LocationMap> getLocMaps() {
        return locMaps;
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

    public void setLocMaps(List<LocationMap> locMaps) {
        this.locMaps = locMaps;
    }
}
