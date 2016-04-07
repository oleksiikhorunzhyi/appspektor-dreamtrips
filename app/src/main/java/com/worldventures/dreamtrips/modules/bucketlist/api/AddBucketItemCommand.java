package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class AddBucketItemCommand extends Command<BucketItem> {
    private BucketBasePostItem bucketPostItem;

    public AddBucketItemCommand(BucketBasePostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().createItem(bucketPostItem);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_add_bl_item;
    }
}
