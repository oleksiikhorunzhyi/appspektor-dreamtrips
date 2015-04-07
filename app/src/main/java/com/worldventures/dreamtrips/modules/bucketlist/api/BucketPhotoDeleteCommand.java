package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

import org.json.JSONObject;

public class BucketPhotoDeleteCommand extends DreamTripsRequest<JSONObject> {

    public BucketPhotoDeleteCommand() {
        super(JSONObject.class);
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        //  return getService().deleteBucketPhoto();
        return null;
    }
}
