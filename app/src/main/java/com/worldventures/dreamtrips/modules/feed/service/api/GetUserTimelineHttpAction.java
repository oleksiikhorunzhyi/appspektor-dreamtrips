package com.worldventures.dreamtrips.modules.feed.service.api;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;

@HttpAction(value = "/api/social/users/{user_id}/timeline", method = HttpAction.Method.GET)
public class GetUserTimelineHttpAction extends GetFeedHttpAction {

   @Path("user_id") int userId;

   public GetUserTimelineHttpAction(int userId, int perPage, String before) {
      super(perPage, before);
      this.userId = userId;
   }
}
