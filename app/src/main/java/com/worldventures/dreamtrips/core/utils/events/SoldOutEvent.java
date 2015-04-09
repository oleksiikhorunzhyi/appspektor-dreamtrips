package com.worldventures.dreamtrips.core.utils.events;

public class SoldOutEvent {

    private boolean soldOut;

    public SoldOutEvent(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }
}
