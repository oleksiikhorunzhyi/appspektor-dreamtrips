package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class UnfriendCommand extends Command<JSONObject> {

   private int userId;

   public UnfriendCommand(int userId) {
      super(JSONObject.class);
      this.userId = userId;
   }

   @Override
   public JSONObject loadDataFromNetwork() throws Exception {
      return getService().unfriend(userId);
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_failed_to_unfriend_user;
   }
}
