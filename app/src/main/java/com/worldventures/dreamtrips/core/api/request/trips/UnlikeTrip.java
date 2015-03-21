package com.worldventures.dreamtrips.core.api.request.trips;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;

public class UnlikeTrip extends DreamTripsRequest<JsonObject> {

    private int photoId;

    public UnlikeTrip(int photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().unlikeTrio(photoId);
    }
}
