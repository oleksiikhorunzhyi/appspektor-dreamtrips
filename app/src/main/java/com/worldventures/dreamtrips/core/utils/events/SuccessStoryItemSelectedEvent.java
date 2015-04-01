package com.worldventures.dreamtrips.core.utils.events;

public class SuccessStoryItemSelectedEvent {

    protected int position;

    public SuccessStoryItemSelectedEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
