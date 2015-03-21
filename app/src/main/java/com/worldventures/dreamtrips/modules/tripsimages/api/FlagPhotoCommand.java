package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class FlagPhotoCommand extends Command<JsonObject> {

    private String reason;
    private int photoId;

    public FlagPhotoCommand(int photoId, String reason) {
        super(JsonObject.class);
        this.reason = reason;
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().flagPhoto(photoId, reason);
    }
}
