package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}", method = HttpAction.Method.PATCH)
public class UpdateItemHttpAction extends AuthorizedHttpAction {
    @Path("uid")
    String uid;

    @Body
    BucketBody bucketPostItem;

    @Response
    BucketItem response;

    public UpdateItemHttpAction(BucketBody body) {
        this.uid = body.id();
        this.bucketPostItem = body;
    }

    public BucketItem getResponse() {
        return this.response;
    }
}
