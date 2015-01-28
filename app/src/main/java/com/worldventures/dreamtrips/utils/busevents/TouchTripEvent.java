package com.worldventures.dreamtrips.utils.busevents;

public class TouchTripEvent {
    int position;

    public TouchTripEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
