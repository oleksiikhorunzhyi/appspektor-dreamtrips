package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.ArrayList;

public class GetBucketItemsQuery extends Query<ArrayList<BucketItem>> {

    public GetBucketItemsQuery() {
        super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
    }

    @Override
    public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
        return getService().getBucketListFull();
    }
}
