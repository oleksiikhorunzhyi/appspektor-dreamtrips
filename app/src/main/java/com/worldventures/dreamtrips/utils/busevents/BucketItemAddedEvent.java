package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.bucket.BucketItem;

/**
 * Created by 1 on 13.03.15.
 */
public class BucketItemAddedEvent {
    private BucketItem bucketItem;

    public BucketItemAddedEvent(BucketItem bucketItem) {
        this.bucketItem = bucketItem;
    }

    public BucketItem getBucketItem() {
        return bucketItem;
    }

    public void setBucketItem(BucketItem bucketItem) {
        this.bucketItem = bucketItem;
    }
}
