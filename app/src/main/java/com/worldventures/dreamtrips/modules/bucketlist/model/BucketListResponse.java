package com.worldventures.dreamtrips.modules.bucketlist.model;

import java.util.List;

public class BucketListResponse {

    List<BucketItem> data;

    public List<BucketItem> getData() {
        return data;
    }

    public void setData(List<BucketItem> data) {
        this.data = data;
    }
}
