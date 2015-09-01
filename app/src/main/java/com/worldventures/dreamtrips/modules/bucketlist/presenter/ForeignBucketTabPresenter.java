package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;

import javax.inject.Inject;

import icepick.Icicle;

public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @Icicle
    protected int userId;

    @Inject
    ForeignBucketItemManager bucketItemManager;

    public ForeignBucketTabPresenter(int userId) {
        this.userId = userId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        getBucketItemManager().setUserId(userId);
    }

    @Override
    protected ForeignBucketItemManager getBucketItemManager() {
        return bucketItemManager;
    }
}
