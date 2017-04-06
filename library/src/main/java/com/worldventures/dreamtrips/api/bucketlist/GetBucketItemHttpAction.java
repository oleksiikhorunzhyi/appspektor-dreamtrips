package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.entity.GetEntityHttpAction;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction("/api/{uid}")
public class GetBucketItemHttpAction extends GetEntityHttpAction<BucketItemSocialized> {

    public GetBucketItemHttpAction(String uid) {
        super(uid);
    }

}
