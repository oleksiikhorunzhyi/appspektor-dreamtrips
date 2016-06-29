package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items", method = HttpAction.Method.POST)
public class CreateBucketItemHttpAction extends AuthorizedHttpAction {
    @Body
    BucketBody bucketPostBody;

    @Response
    BucketItem response;

    public CreateBucketItemHttpAction(BucketBody bucketPostBody) {
        this.bucketPostBody = bucketPostBody;
    }

    public BucketItem getResponse() {
        return this.response;
    }
}