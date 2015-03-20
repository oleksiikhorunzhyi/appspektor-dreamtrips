package com.worldventures.dreamtrips.utils.events;

import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;

/**
 * Created by 1 on 13.03.15.
 */
public class BucketItemReloadEvent {
    private BucketPostItem bucketPostItem;

    public BucketItemReloadEvent(BucketPostItem bucketPostItem) {
        this.bucketPostItem = bucketPostItem;
    }

    public BucketPostItem getBucketPostItem() {
        return bucketPostItem;
    }

    public void setBucketPostItem(BucketPostItem bucketPostItem) {
        this.bucketPostItem = bucketPostItem;
    }
}
