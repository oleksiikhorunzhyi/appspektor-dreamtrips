package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class LikePhotoCommand extends Command<JsonObject> {

    private int photoId;

    public LikePhotoCommand(int photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().likePhoto(photoId);
    }
}
