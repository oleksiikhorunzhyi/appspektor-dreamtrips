package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketOrderBody;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketOrderBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}/position", method = HttpAction.Method.PUT)
public class ChangeOrderHttpAction extends AuthorizedHttpAction {

   @Path("uid") String uid;

   @Body BucketOrderBody item;

   @Response JsonObject response;

   public ChangeOrderHttpAction(String uid, int to) {
      this.uid = uid;
      this.item = ImmutableBucketOrderBody.of(to);
   }

   public JsonObject getResponse() {
      return this.response;
   }
}
