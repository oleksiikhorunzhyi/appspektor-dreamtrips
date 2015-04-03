package com.worldventures.dreamtrips.modules.trips.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class UnlikeTripCommand extends DreamTripsRequest<JsonObject> {

    private String photoId;

    public UnlikeTripCommand(String photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().unlikeTrio(photoId);
    }
}
