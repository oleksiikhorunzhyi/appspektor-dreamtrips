package com.worldventures.dreamtrips.utils.busevents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 22.01.15.
 */
public class FilterBusEvent {

    private int minNights;
    private int maxNights;
    private double minPrice;
    private double maxPrice;
    private List<Integer> acceptedRegions = new ArrayList<>();

    public int getMinNights() {
        return minNights;
    }

    public void setMinNights(int minNights) {
        this.minNights = minNights;
    }

    public int getMaxNights() {
        return maxNights;
    }

    public void setMaxNights(int maxNights) {
        this.maxNights = maxNights;
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

    public List<Integer> getAcceptedRegions() {
        return acceptedRegions;
    }

    public void setAcceptedRegions(List<Integer> acceptedRegions) {
        this.acceptedRegions = acceptedRegions;
    }
}
