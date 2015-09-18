package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.modules.bucketlist.manager.ForeignBucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import dagger.ObjectGraph;

public class ForeignBucketListPresenter extends BucketListPresenter {

    public ForeignBucketListPresenter(BucketItem.BucketType type, ObjectGraph objectGraph) {
        super(type, objectGraph);
    }


    @NonNull
    @Override
    protected Class<ForeignBucketItemManager> getBucketItemManagerClass() {
        return ForeignBucketItemManager.class;
    }
}
