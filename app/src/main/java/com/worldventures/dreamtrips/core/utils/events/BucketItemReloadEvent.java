package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;

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
