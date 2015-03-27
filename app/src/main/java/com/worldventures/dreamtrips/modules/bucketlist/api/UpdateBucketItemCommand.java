package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;

public class UpdateBucketItemCommand extends Command<BucketItem> {

    private BucketPostItem bucketPostItem;
    private int id;

    public UpdateBucketItemCommand(int id, BucketPostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
        this.id = id;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().updateItem(id, bucketPostItem);
    }
}