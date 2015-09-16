package com.worldventures.dreamtrips.modules.bucketlist.event;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

public class BucketTabChangedEvent {

    public final BucketItem.BucketType type;

    public BucketTabChangedEvent(BucketItem.BucketType type) {
        this.type = type;
    }
}
