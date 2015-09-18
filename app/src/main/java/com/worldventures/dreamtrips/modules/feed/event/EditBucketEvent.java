package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

public class EditBucketEvent {

    private String uid;
    private BucketTabsPresenter.BucketType type;

    public EditBucketEvent(String uid, BucketTabsPresenter.BucketType type) {
        this.uid = uid;
        this.type = type;
    }

    public BucketTabsPresenter.BucketType getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }
}
