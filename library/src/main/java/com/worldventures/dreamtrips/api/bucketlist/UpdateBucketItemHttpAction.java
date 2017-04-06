package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.PATCH;

@HttpAction(value = "/api/bucket_list_items/{uid}", method = PATCH)
public class UpdateBucketItemHttpAction extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Body
    public final BucketUpdateBody body;

    @Response
    BucketItemSocialized item;

    public UpdateBucketItemHttpAction(String uid, BucketUpdateBody body) {
        this.uid = uid;
        this.body = body;
    }

    public BucketItemSocialized response() {
        return item;
    }
}
