package com.worldventures.dreamtrips.modules.bucketlist.api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;

import java.util.ArrayList;
import java.util.List;

public class ReorderBucketItemCommand extends Command<JsonObject> {
    private BucketOrderModel bucketOrderModel;

    public ReorderBucketItemCommand(BucketOrderModel bucketOrderModel) {
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
