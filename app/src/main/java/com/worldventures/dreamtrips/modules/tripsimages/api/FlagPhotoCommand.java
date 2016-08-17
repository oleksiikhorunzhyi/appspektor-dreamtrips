package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

public class FlagPhotoCommand extends Command<JsonObject> {

   private String reason;
   private String photoId;

   public FlagPhotoCommand(String photoId, String reason) {
      super(JsonObject.class);
      this.reason = reason;
      this.photoId = photoId;
   }

   @Override
   public JsonObject loadDataFromNetwork() throws Exception {
      return getService().flagPhoto(photoId, reason);
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_flag_item;
   }
}
