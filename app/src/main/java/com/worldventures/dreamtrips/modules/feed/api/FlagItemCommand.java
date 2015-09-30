package com.worldventures.dreamtrips.modules.feed.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class FlagItemCommand extends Command<JsonObject> {
    private String uid;
    private String nameOfReason;

    public FlagItemCommand(String uid, String nameOfReason) {
        super(JsonObject.class);
        this.uid = uid;
        this.nameOfReason = nameOfReason;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().flagItem(uid, nameOfReason);
    }
}
