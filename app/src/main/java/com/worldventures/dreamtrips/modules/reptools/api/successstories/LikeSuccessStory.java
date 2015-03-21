package com.worldventures.dreamtrips.modules.reptools.api.successstories;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.DreamTripsRequest;

public class LikeSuccessStory extends DreamTripsRequest<JsonObject> {
    private int ssId;

    public LikeSuccessStory(int ssId) {
        super(JsonObject.class);
        this.ssId = ssId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().likeSS(ssId);
    }
}
