package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;

import javax.inject.Inject;

public class ForeignBucketItemDetailsPresenter extends BucketItemDetailsPresenter {

    @Inject
    ForeignBucketItemManager foreignBucketItemManager;

    public ForeignBucketItemDetailsPresenter(Bundle bundle) {
        super(bundle);
    }

    @Override
    protected BucketItemManager getBucketItemManager() {
        return foreignBucketItemManager;
    }
}
