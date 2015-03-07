package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.BucketItem;

/**
 * Created by 1 on 05.03.15.
 */
public class MarkBucketItemDoneEvent {
    private BucketItem bucketItem;

    public MarkBucketItemDoneEvent(BucketItem bucketItem) {
        this.bucketItem = bucketItem;
    }

    public BucketItem getBucketItem() {
        return bucketItem;
    }

}
