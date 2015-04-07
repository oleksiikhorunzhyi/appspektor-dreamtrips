package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;

public class MarkBucketItemCommand extends Command<BucketItem> {
    private BucketStatusItem bucketStatusItem;
    private int id;

    public MarkBucketItemCommand(int id, BucketStatusItem bucketStatusItem) {
        super(BucketItem.class);
        this.bucketStatusItem = bucketStatusItem;
        this.id = id;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().completeItem(id, bucketStatusItem);
    }
}
