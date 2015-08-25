package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import icepick.Icicle;

public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @Icicle
    protected int userId;

    public ForeignBucketTabPresenter(int userId) {
        this.userId = userId;
    }

    @Override
    public void onResume() {
        bucketItemManager.setUserId(userId);
        super.onResume();
    }
}
