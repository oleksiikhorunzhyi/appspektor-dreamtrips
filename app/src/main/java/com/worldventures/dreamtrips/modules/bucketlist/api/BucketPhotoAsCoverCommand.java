package com.worldventures.dreamtrips.modules.bucketlist.api;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;

import org.json.JSONObject;

public class BucketPhotoAsCoverCommand extends DreamTripsRequest<JSONObject> {
    public BucketPhotoAsCoverCommand() {
        super(JSONObject.class);
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        return null;
    }
}
