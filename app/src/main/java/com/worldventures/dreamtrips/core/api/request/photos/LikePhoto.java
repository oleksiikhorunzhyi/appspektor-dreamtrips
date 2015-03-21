package com.worldventures.dreamtrips.core.api.request.photos;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;

public class LikePhoto extends DreamTripsRequest<JsonObject> {

    private int photoId;

    public LikePhoto(int photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().likePhoto(photoId);
    }
}
