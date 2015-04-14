package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeleteBucketPhotoCommand extends Command<JsonObject> {

    private String photoId;
    private int bucketId;

    public DeleteBucketPhotoCommand(String photoId, int bucketId) {
        super(JsonObject.class);
        this.bucketId = bucketId;
        this.photoId = photoId;
    }

    @Override
    public JsonObject loadDataFromNetwork() throws Exception {
        return getService().deleteBucketPhoto(bucketId, photoId);
    }
}
