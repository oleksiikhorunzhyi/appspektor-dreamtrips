package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketPhotoBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/bucket_list_items/{uid}/photos", method = POST)
public class AddPhotoToBucketItemHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Body
    public final BucketPhotoBody body;

    @Response
    BucketPhoto response;

    public AddPhotoToBucketItemHttpAction(String uid, BucketPhotoBody body) {
        this.uid = uid;
        this.body = body;
    }

    public BucketPhoto response() {
        return response;
    }

}
