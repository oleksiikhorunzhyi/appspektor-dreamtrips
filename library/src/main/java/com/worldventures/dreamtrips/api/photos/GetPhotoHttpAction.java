package com.worldventures.dreamtrips.api.photos;

import com.worldventures.dreamtrips.api.entity.GetEntityHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction("/api/{uid}")
public class GetPhotoHttpAction extends GetEntityHttpAction<PhotoSocialized> {

    public GetPhotoHttpAction(String uid) {
        super(uid);
    }
}
