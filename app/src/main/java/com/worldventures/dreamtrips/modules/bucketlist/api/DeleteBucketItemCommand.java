package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeleteBucketItemCommand extends Command<JsonObject> {
    private String uid;

    public DeleteBucketItemCommand(String uid) {
        super(JsonObject.class);
        this.uid = uid;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        return getService().deleteItem(uid);
    }
}
