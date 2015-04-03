package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeletePhotoCommand extends Command<JsonObject> {

    private String photoId;

    public DeletePhotoCommand(String photoId) {
        super(JsonObject.class);
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().deletePhoto(photoId);
    }
}
