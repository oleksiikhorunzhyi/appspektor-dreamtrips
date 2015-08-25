package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import icepick.Icicle;

public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @Icicle
    protected String userId;

    public ForeignBucketTabPresenter(String userId) {
        this.userId = userId;
    }

    @Override
    public void onResume() {
        bucketItemManager.setUserId(userId);
        super.onResume();
    }
}
