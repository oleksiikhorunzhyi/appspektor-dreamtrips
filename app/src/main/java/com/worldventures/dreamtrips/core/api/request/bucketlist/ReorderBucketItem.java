package com.worldventures.dreamtrips.core.api.request.bucketlist;

import android.util.Log;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.bucket.BucketOrderModel;

import java.util.ArrayList;
import java.util.List;

public class ReorderBucketItem extends DreamTripsRequest<JsonObject> {
    private BucketOrderModel bucketOrderModel;

    public ReorderBucketItem(BucketOrderModel bucketOrderModel) {
        super(JsonObject.class);
        this.bucketOrderModel = bucketOrderModel;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        Log.d("TAG_BucketListPM", "Sending delete item event");
        List<BucketOrderModel> list = new ArrayList<>();
        list.add(bucketOrderModel);
        return getService().changeOrder(list);
    }
}
