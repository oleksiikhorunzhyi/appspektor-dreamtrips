package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;

public class AddBucketItem extends DreamTripsRequest<BucketItem> {
    private BucketPostItem bucketPostItem;

    public AddBucketItem(BucketPostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().createItem(bucketPostItem);
    }
}
