package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;

import javax.inject.Inject;

public class ForeignBucketItemDetailsPresenter extends BucketItemDetailsPresenter {

    @Inject
    ForeignBucketItemManager foreignBucketItemManager;

    public ForeignBucketItemDetailsPresenter(BucketBundle bundle) {
        super(bundle);
    }

    @Override
    protected BucketItemManager getBucketItemManager() {
        return foreignBucketItemManager;
    }
}
