package com.worldventures.dreamtrips.core.api.request.photos;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class DeletePhoto extends DreamTripsRequest<JsonObject> {

    private int photoId;

    public DeletePhoto(int photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().deletePhoto(photoId);
    }
}
