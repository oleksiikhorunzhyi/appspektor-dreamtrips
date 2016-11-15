package com.worldventures.dreamtrips.modules.feed.service.api;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;

@HttpAction(value = "/api/social/notifications", method = HttpAction.Method.GET)
public class GetNotificationFeedHttpAction extends GetFeedHttpAction {

   public GetNotificationFeedHttpAction(int perPage, String before) {
      super(perPage, before);
   }
}
