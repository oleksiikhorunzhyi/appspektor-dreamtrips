package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.ArrayList;

public class GetBucketItemsQuery extends Query<ArrayList<BucketItem>> {

    String userId;

    public GetBucketItemsQuery(String userId) {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
        this.userId = userId;
    }

    public GetBucketItemsQuery() {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
    }

    @Override
    public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
        if (userId != null) {
            return getService().getBucketListFull(userId);
        }
        return getService().getBucketListFull();
    }
}
