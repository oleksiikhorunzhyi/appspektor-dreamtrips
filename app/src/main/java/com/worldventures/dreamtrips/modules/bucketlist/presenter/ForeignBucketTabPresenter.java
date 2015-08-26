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
    public void onResume() {
        getBucketItemManager().setUserId(userId);
        super.onResume();
    }


    @Override
    protected ForeignBucketItemManager getBucketItemManager() {
        return bucketItemManager;
    }
}
