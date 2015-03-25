package com.worldventures.dreamtrips.core.utils.events;


public class RangeBarDurationEvent {

    private int minNights;
    private int maxNights;

    public RangeBarDurationEvent(int minNights, int maxNights) {
        this.minNights = minNights;
        this.maxNights = maxNights;
    }

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
}
