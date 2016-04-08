package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;

public class ReorderBucketItemCommand extends Command<JsonObject> {
    private BucketOrderModel bucketOrderModel;
    private String uid;

    public ReorderBucketItemCommand(String uid, BucketOrderModel bucketOrderModel) {
        super(JsonObject.class);
        this.bucketOrderModel = bucketOrderModel;
        this.uid = uid;
    }

    @Override
    public JsonObject loadDataFromNetwork(){
        return getService().changeOrder(uid, bucketOrderModel);
    }
}
