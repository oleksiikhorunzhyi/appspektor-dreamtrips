package com.worldventures.dreamtrips.api.bucketlist;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(value = "/api/bucket_list_items/{item_uid}/photos/{photo_uid}", method = DELETE)
public class DeletePhotoFromBucketItemHttpAction extends AuthorizedHttpAction {

    @Path("item_uid")
    public final String itemUid;

    @Path("photo_uid")
    public final String photoUid;

    public DeletePhotoFromBucketItemHttpAction(String itemUid, String photoUid) {
        this.itemUid = itemUid;
        this.photoUid = photoUid;
    }

}
