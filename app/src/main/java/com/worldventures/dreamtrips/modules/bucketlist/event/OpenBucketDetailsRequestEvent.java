package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

public class OpenBucketDetailsRequestEvent {
    private BucketTabsPresenter.BucketType type;
    private String bucketItemId;

    public OpenBucketDetailsRequestEvent(BucketTabsPresenter.BucketType type, String bucketItemId) {
        this.type = type;
        this.bucketItemId = bucketItemId;
    }

    public BucketTabsPresenter.BucketType getType() {
        return type;
    }

    public String getBucketItemId() {
        return bucketItemId;
    }
}
