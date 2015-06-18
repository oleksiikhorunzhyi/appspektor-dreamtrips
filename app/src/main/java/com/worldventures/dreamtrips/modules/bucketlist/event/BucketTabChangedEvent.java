package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

public class BucketTabChangedEvent {

    public final BucketTabsPresenter.BucketType type;

    public BucketTabChangedEvent(BucketTabsPresenter.BucketType type) {
        this.type = type;
    }
}
