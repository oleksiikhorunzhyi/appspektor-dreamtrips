package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

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
