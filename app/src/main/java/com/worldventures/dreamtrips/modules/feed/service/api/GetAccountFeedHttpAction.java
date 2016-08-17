package com.worldventures.dreamtrips.modules.feed.service.api;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;

@HttpAction(value = "/api/social/feed", method = HttpAction.Method.GET)
public class GetAccountFeedHttpAction extends GetFeedHttpAction {

   @Query("circle_id") String circleId;

   public GetAccountFeedHttpAction(String circleId, int perPage, String before) {
      super(perPage, before);
      this.circleId = circleId;
   }
}
