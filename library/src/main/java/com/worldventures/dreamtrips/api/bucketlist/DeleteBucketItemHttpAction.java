package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/bucket_list_items/{uid}", method = DELETE)
public class DeleteBucketItemHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    public DeleteBucketItemHttpAction(String uid) {
        this.uid = uid;
    }
}
