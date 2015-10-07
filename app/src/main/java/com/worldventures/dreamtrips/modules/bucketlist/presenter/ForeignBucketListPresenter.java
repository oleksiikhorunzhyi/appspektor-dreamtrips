package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import dagger.ObjectGraph;

public class ForeignBucketListPresenter extends BucketListPresenter {

    public ForeignBucketListPresenter(BucketItem.BucketType type, ObjectGraph objectGraph) {
        super(type, objectGraph);
    }

}
