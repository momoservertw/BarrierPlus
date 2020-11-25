package tw.momocraft.barrierplus.utils;


public class BuyMap {
    private String priceType;
    private double price;
    private int amount;


    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPriceType() {
        return priceType;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}
