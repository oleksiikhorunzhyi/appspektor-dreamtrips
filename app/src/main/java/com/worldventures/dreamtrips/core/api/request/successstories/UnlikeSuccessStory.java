package com.worldventures.dreamtrips.core.api.request.successstories;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;

public class UnlikeSuccessStory extends DreamTripsRequest<JsonObject> {

    private int ssId;

    public UnlikeSuccessStory(int ssId) {
        super(JsonObject.class);
        this.ssId = ssId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().unlikeSS(ssId);
    }
}
