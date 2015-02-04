package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.BucketItem;

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
