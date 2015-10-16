package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.common.model.User;

import javax.inject.Inject;

import icepick.State;


public class ForeignBucketTabPresenter extends BucketTabsPresenter {

    @State
    User user;

    @Inject
    BucketItemManager bucketItemManager;

    public ForeignBucketTabPresenter(User user) {
        this.user = user;
    }


    @Override
    protected User getUser() {
        return user;
    }
}
