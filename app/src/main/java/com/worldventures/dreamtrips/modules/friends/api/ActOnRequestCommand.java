package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

public class ActOnRequestCommand extends Command<JSONObject> {
   private int userId;
   private String action;
   private String circleID;

   public ActOnRequestCommand(int userId, String action) {
      this(userId, action, null);
   }

   public ActOnRequestCommand(int userId, String action, String circleID) {
      super(JSONObject.class);
      this.userId = userId;
      this.action = action;
      this.circleID = circleID;
   }

   @Override
   public JSONObject loadDataFromNetwork() throws Exception {
      return getService().actOnRequest(userId, action, circleID);
   }

   @Override
   public int getErrorMessage() {
      return action.equals(Action.CONFIRM.name()) ? R.string.error_fail_to_accept_friend_request : R.string.error_fail_to_reject_friend_request;
   }

   public enum Action {
      CONFIRM, REJECT
   }
}
