package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class UpdateBucketItemCommand extends Command<BucketItem> {

    private BucketBasePostItem bucketPostItem;
    private String id;

    public UpdateBucketItemCommand(String id, BucketBasePostItem bucketPostItem) {
        super(BucketItem.class);
        this.bucketPostItem = bucketPostItem;
        this.id = id;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().updateItem(id, bucketPostItem);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_edit_bl;
    }
}