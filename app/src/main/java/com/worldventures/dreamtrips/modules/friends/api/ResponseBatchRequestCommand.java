package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseBatchRequestCommand extends Command<ArrayList<JSONObject>> {

   RequestBody body;

   public ResponseBatchRequestCommand(List<RequestEntity> responses) {
      super((Class<ArrayList<JSONObject>>) new ArrayList<JSONObject>().getClass());
      this.body = new RequestBody(responses);
   }

   @Override
   public ArrayList<JSONObject> loadDataFromNetwork() throws Exception {
      return getService().actOnBatchRequests(body);
   }

   public static class RequestEntity {
      int userId;
      String action;
      String circleId;

      public RequestEntity(int userId, String action, String circleId) {
         this.userId = userId;
         this.action = action;
         this.circleId = circleId;
      }
   }


   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_accept_friend_requests;
   }

   public static class RequestBody {
      List<RequestEntity> responses;

      public RequestBody(List<RequestEntity> responses) {
         this.responses = responses;
      }
   }
}
