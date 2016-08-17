package com.worldventures.dreamtrips.modules.membership.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/podcasts", method = HttpAction.Method.GET)
public class GetPodcastsHttpAction extends AuthorizedHttpAction {

   @Query("page") int page;
   @Query("per_page") int perPage;

   @Response List<Podcast> responseItems;

   public GetPodcastsHttpAction(int page, int perPage) {
      this.page = page;
      this.perPage = perPage;
   }

   public List<Podcast> getResponseItems() {
      if (responseItems == null) responseItems = new ArrayList<>();
      return responseItems;
   }
}
