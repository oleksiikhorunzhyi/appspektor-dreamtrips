package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class HashtagInteractor {

   private final ActionPipe<HashtagSuggestionCommand> suggestionPipe;
   private final ActionPipe<FeedByHashtagCommand.Refresh> refreshFeedsByHashtagsPipe;
   private final ActionPipe<FeedByHashtagCommand.LoadNext> loadNextFeedsByHashtagsPipe;

   @Inject
   public HashtagInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      suggestionPipe = sessionActionPipeCreator.createPipe(HashtagSuggestionCommand.class, Schedulers.io());
      refreshFeedsByHashtagsPipe = sessionActionPipeCreator.createPipe(FeedByHashtagCommand.Refresh.class, Schedulers.io());
      loadNextFeedsByHashtagsPipe = sessionActionPipeCreator.createPipe(FeedByHashtagCommand.LoadNext.class, Schedulers.io());
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
