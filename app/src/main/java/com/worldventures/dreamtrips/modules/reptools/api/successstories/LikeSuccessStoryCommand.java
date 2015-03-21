package com.worldventures.dreamtrips.modules.reptools.api.successstories;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class LikeSuccessStoryCommand extends Command<JsonObject> {
    private int ssId;

    public LikeSuccessStoryCommand(int ssId) {
        super(JsonObject.class);
        this.ssId = ssId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().likeSS(ssId);
    }
}
