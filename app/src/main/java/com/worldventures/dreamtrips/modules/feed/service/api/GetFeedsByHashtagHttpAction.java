package com.worldventures.dreamtrips.modules.feed.service.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.DataMetaData;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/hashtags/search", method = HttpAction.Method.GET)
public class GetFeedsByHashtagHttpAction extends AuthorizedHttpAction {

   @Query("query") String query;
   @Query("per_page") int perPage;
   @Query("before") String before;
   @Query("type") String type = "post";

   @Response DataMetaData responseItems;

   public GetFeedsByHashtagHttpAction(String query, int perPage, String before) {
      this.query = query;
      this.perPage = perPage;
      this.before = before;
   }

   public DataMetaData getResponseItems() {
      if (responseItems == null) responseItems = new DataMetaData();
      return responseItems;
   }
}
