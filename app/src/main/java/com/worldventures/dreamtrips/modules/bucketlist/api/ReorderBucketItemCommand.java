package com.worldventures.dreamtrips.modules.bucketlist.api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;

import java.util.ArrayList;
import java.util.List;

public class ReorderBucketItemCommand extends Command<JsonObject> {
    private BucketOrderModel bucketOrderModel;
    private int id;

    public ReorderBucketItemCommand(int id, BucketOrderModel bucketOrderModel) {
        super(JsonObject.class);
        this.bucketOrderModel = bucketOrderModel;
        this.id = id;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        Log.d("TAG_BucketListPM", "Sending delete item event");
        return getService().changeOrder(id, bucketOrderModel);
    }
}
