package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(value = "/api/bucket_list_items", method = POST)
public class CreateBucketItemHttpAction extends AuthorizedHttpAction {

    @Body
    public final BucketCreationBody body;

    @Response
    BucketItemSocialized item;

    public CreateBucketItemHttpAction(BucketCreationBody body) {
        this.body = body;
    }

    public BucketItemSocialized response() {
        return item;
    }

}
