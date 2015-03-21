package com.worldventures.dreamtrips.core.api.request.bucketlist;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;

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
