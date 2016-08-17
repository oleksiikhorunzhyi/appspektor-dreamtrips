package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/bucket_list_items/{uid}/photos/{photo_id}", method = HttpAction.Method.DELETE)
public class DeleteBucketPhotoHttpAction extends AuthorizedHttpAction {

   @Path("uid") String uid;

   @Path("photo_id") String photoId;

   @Response JsonObject response;

   public DeleteBucketPhotoHttpAction(String uid, String photoId) {
      this.uid = uid;
      this.photoId = photoId;
   }

   public JsonObject getResponse() {
      return this.response;
   }
}