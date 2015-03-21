package com.worldventures.dreamtrips.core.api.request.photos;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class FlagPhoto extends DreamTripsRequest<JsonObject> {

    private String reason;
    private int photoId;

    public FlagPhoto(int photoId, String reason) {
        super(JsonObject.class);
        this.reason = reason;
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().flagPhoto(photoId, reason);
    }
}
