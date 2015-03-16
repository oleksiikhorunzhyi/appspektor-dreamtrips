package com.worldventures.dreamtrips.utils.busevents;

public class SuccessStoryItemSelectedEvent {

    int position;

    public SuccessStoryItemSelectedEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
