package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;

public class MarkBucketItemCommand extends Command<BucketItem> {

    private BucketStatusItem bucketStatusItem;
    private String uid;

    public MarkBucketItemCommand(String uid, BucketStatusItem bucketStatusItem) {
        super(BucketItem.class);
        this.bucketStatusItem = bucketStatusItem;
        this.uid = uid;
    }

    @Override
    public BucketItem loadDataFromNetwork() {
        return getService().completeItem(uid, bucketStatusItem);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_edit_bl;
    }
}
