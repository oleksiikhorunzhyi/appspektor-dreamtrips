package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.ArrayList;

public class GetBucketItemsQuery extends Query<ArrayList<BucketItem>> {

    int userId;

    public GetBucketItemsQuery(int userId) {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
        this.userId = userId;
    }

    public GetBucketItemsQuery() {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
    }

    @Override
    public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
        if (userId != 0) {
            return getService().getBucketListFull(userId);
        }
        return getService().getBucketListFull();
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_bl;
    }
}
