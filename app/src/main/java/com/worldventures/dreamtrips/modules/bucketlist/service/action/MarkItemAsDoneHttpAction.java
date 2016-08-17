package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketStatusBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketStatusBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}", method = HttpAction.Method.PATCH)
public class MarkItemAsDoneHttpAction extends AuthorizedHttpAction {

   @Path("uid") String uid;

   @Body BucketStatusBody bucketPostItem;

   @Response BucketItem response;

   public MarkItemAsDoneHttpAction(String uid, String status) {
      this.uid = uid;
      this.bucketPostItem = ImmutableBucketStatusBody.of(status);
   }

   public BucketItem getResponse() {
      return this.response;
   }
}
