package com.worldventures.dreamtrips.utils.events;

/**
 * Created by 1 on 22.01.15.
 */
public class RangeBarPriceEvent {

    private double minPrice;
    private double maxPrice;

    public RangeBarPriceEvent(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }
}
