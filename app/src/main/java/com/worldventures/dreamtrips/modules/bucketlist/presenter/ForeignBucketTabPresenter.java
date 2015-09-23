package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;

import javax.inject.Inject;

import icepick.State;


public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @State
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
