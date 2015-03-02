package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.BucketItem;

/**
 * Created by 1 on 02.03.15.
 */
public class DeleteBucketItemEvent {

    private BucketItem bucketItem;

    public DeleteBucketItemEvent(BucketItem bucketItem) {
        this.bucketItem = bucketItem;
    }

    public BucketItem getBucketItem() {
        return bucketItem;
    }
}
