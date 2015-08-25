package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

public class OpenBucketDetailsRequestEvent {
    private BucketTabsPresenter.BucketType type;
    private int bucketItemId;

    public OpenBucketDetailsRequestEvent(BucketTabsPresenter.BucketType type, int bucketItemId) {
        this.type = type;
        this.bucketItemId = bucketItemId;
    }

    public BucketTabsPresenter.BucketType getType() {
        return type;
    }

    public int getBucketItemId() {
        return bucketItemId;
    }
}
