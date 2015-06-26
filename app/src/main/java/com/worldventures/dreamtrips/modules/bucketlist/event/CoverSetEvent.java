package com.worldventures.dreamtrips.modules.bucketlist.event;

public class CoverSetEvent {

    int coverId;

    public CoverSetEvent(int coverId) {
        this.coverId = coverId;
    }

    public int getCoverId() {
        return coverId;
    }
}
