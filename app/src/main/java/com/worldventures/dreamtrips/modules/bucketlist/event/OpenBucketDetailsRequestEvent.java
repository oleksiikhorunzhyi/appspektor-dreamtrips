package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class OpenBucketDetailsRequestEvent {
    private BucketItem.BucketType type;
    private String bucketItemId;

    public OpenBucketDetailsRequestEvent(BucketItem.BucketType type, String bucketItemId) {
        this.type = type;
        this.bucketItemId = bucketItemId;
    }

    public BucketItem.BucketType getType() {
        return type;
    }

    public String getBucketItemId() {
        return bucketItemId;
    }
}
