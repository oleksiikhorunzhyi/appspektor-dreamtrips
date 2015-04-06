package com.worldventures.dreamtrips.modules.bucketlist.api;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;

public class MarkBucketItemCommand extends Command<BucketItem> {
    private BucketPostItem bucketPostItem;
    private int id;

    public MarkBucketItemCommand(int id, BucketPostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
        this.id = id;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        Log.d("TAG_BucketListPM", "Sending mark as done item event");
        return getService().updateItem(id, bucketPostItem);
    }
}
