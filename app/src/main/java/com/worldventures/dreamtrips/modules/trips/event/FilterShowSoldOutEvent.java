package com.worldventures.dreamtrips.modules.trips.event;

public class FilterShowSoldOutEvent {

    private boolean soldOut;

    public FilterShowSoldOutEvent(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }
}
