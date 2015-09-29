package com.worldventures.dreamtrips.modules.bucketlist.event;

public class BucketItemDeleteConfirmedEvent {

    private String bucketItemId;

    public BucketItemDeleteConfirmedEvent(String bucketItemId) {
        this.bucketItemId = bucketItemId;
    }

    public String getBucketItemId() {
        return bucketItemId;
    }
}
