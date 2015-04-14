package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketPhotoFullscreenRequestEvent {
    private int position;

    public BucketPhotoFullscreenRequestEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
