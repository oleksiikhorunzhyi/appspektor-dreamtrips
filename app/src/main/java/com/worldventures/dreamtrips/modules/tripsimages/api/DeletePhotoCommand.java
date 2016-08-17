package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class DeletePhotoCommand extends Command<JsonObject> {

   private String photoId;

   public DeletePhotoCommand(String photoId) {
      super(JsonObject.class);
      this.photoId = photoId;
   }

   @Override
   public JsonObject loadDataFromNetwork() throws Exception {
      return getService().deletePhoto(photoId);
   }


   @Override
   public int getErrorMessage() {
      return R.string.error_failed_to_delete_image;
   }
}
