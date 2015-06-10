package com.worldventures.dreamtrips.core.utils.events;

/**
 *  1 on 05.02.15.
 */
public class MapInfoReadyEvent {

    private int offset;

    public MapInfoReadyEvent(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
