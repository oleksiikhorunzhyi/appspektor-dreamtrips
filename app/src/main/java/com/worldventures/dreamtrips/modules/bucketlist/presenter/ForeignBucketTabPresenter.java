package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;

import javax.inject.Inject;

import icepick.State;


public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @State
    protected int userId;

    @Inject
    BucketItemManager bucketItemManager;

    public ForeignBucketTabPresenter(int userId) {
        this.userId = userId;
    }


    @Override
    protected int getUserId() {
        return userId;
    }
}
