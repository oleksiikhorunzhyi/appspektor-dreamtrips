package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {

   private final ActionPipe<HashtagSuggestionCommand> suggestionPipe;
   private final ActionPipe<FeedByHashtagCommand.Refresh> refreshFeedsByHashtagsPipe;
   private final ActionPipe<FeedByHashtagCommand.LoadNext> loadNextFeedsByHashtagsPipe;

   @Inject
   public HashtagInteractor(Janet janet) {
      suggestionPipe = janet.createPipe(HashtagSuggestionCommand.class);
      refreshFeedsByHashtagsPipe = janet.createPipe(FeedByHashtagCommand.Refresh.class);
      loadNextFeedsByHashtagsPipe = janet.createPipe(FeedByHashtagCommand.LoadNext.class);
   }

   public ActionPipe<HashtagSuggestionCommand> getSuggestionPipe() {
      return suggestionPipe;
   }

   public ActionPipe<FeedByHashtagCommand.Refresh> getRefreshFeedsByHashtagsPipe() {
      return refreshFeedsByHashtagsPipe;
   }

   public ActionPipe<FeedByHashtagCommand.LoadNext> getLoadNextFeedsByHashtagsPipe() {
      return loadNextFeedsByHashtagsPipe;
   }
}
