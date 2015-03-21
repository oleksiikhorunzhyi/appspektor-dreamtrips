package com.worldventures.dreamtrips.core.api.request.bucketlist;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;

public class MarkBucketItem extends DreamTripsRequest<BucketItem> {
    private BucketPostItem bucketPostItem;
    private int id;

    public MarkBucketItem(int id, BucketPostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
        this.id = id;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        Log.d("TAG_BucketListPM", "Sending mark as done item event");
        return getService().markItem(id, bucketPostItem);
    }
}
