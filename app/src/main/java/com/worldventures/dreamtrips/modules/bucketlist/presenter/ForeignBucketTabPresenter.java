package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;

import icepick.State;

public class ForeignBucketTabPresenter extends BucketTabsPresenter {
    @State
    User user;

    public ForeignBucketTabPresenter(User user) {
        this.user = user;
    }


    @Override
    protected User getUser() {
        return user;
    }
}
