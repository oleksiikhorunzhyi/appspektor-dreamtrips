package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}/photos", method = HttpAction.Method.POST)
public class UploadPhotoHttpAction extends AuthorizedHttpAction {
    @Path("uid")
    String uid;

    @Body
    BucketPhoto photo;

    @Response
    BucketPhoto responsePhoto;

    public UploadPhotoHttpAction(String uid, BucketPhoto photo) {
        this.uid = uid;
        this.photo = photo;
    }

    public BucketPhoto getResponse() {
        return responsePhoto;
    }
}