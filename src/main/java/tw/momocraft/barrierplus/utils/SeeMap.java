package tw.momocraft.barrierplus.utils;

public class SeeMap {
    private String creative;
    private String particle;
    private int amount;
    private int times;
    private int interval;

    public int getAmount() {
        return amount;
    }

    public int getTimes() {
        return times;
    }

    public String getCreative() {
        return creative;
    }

    public int getInterval() {
        return interval;
    }

    public String getParticle() {
        return particle;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setCreative(String creative) {
        this.creative = creative;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }
}
