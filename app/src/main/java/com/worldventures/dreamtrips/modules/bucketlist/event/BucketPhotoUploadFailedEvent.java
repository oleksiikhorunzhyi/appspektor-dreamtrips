package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketPhotoUploadFailedEvent {
    private int bucketId;

    public BucketPhotoUploadFailedEvent(int bucketId) {

        this.bucketId = bucketId;
    }
}
