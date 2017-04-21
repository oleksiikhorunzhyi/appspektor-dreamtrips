package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.hashtags.GetHashtagsSuggestsAction;
import com.worldventures.dreamtrips.api.hashtags.model.ImmutableHashtagsSuggestsParams;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class HashtagSuggestionCommand extends MappableApiActionCommand<GetHashtagsSuggestsAction, List<HashtagSuggestion>, HashtagSuggestion> {

   private static final int PAGE = 1;
   private static final int PER_PAGE = 10;

   private String fullQueryText;
   private String query;

   public HashtagSuggestionCommand(String fullText, String query) {
      this.fullQueryText = fullText;
      this.query = query;
   }

   @Override
   protected Class<HashtagSuggestion> getMappingTargetClass() {
      return HashtagSuggestion.class;
   }

   @Override
   protected Object mapHttpActionResult(GetHashtagsSuggestsAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected GetHashtagsSuggestsAction getHttpAction() {
      return new GetHashtagsSuggestsAction(ImmutableHashtagsSuggestsParams.builder()
            .query(query)
            .page(PAGE)
            .perPage(PER_PAGE)
            .build());
   }

   @Override
   protected Class<GetHashtagsSuggestsAction> getHttpActionClass() {
      return GetHashtagsSuggestsAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_something_went_wrong;
   }

   public String getFullQueryText() {
      return fullQueryText;
   }
}