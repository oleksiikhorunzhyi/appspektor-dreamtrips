package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;

public class AddBucketItemCommand extends Command<BucketItem> {
    private BucketPostItem bucketPostItem;

    public AddBucketItemCommand(BucketPostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().createItem(bucketPostItem);
    }
}
