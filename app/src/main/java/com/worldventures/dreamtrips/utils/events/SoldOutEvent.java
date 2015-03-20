package com.worldventures.dreamtrips.utils.events;

/**
 * Created by 1 on 23.01.15.
 */
public class SoldOutEvent {

    private boolean isSoldOut;

    public SoldOutEvent(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }
}
