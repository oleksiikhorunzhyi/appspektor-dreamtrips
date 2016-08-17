package com.worldventures.dreamtrips.modules.feed.service.api;


import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/hashtags/suggestions", method = HttpAction.Method.GET)
public class HashtagSuggestionHttpAction extends AuthorizedHttpAction {

   @Query("query") String query;

   @Response List<HashtagSuggestion> hashtagSuggestions;

   public HashtagSuggestionHttpAction(String query) {
      this.query = query;
   }

   public List<HashtagSuggestion> hashtagSuggestions() {
      return hashtagSuggestions;
   }
}
