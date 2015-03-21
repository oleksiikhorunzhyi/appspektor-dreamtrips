package com.worldventures.dreamtrips.core.api.request.photos;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

public class UnlikePhoto extends DreamTripsRequest<JsonObject> {

    private int photoId;

    public UnlikePhoto(int photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().unlikePhoto(photoId);
    }
}
