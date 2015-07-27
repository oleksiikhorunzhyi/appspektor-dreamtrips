package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeleteBucketItemCommand extends Command<JsonObject> {
    private int id;

    public DeleteBucketItemCommand(int id) {
        super(JsonObject.class);
        this.id = id;
    }

    @Override
    public JsonObject loadDataFromNetwork() {
        return getService().deleteItem(id);
    }
}
