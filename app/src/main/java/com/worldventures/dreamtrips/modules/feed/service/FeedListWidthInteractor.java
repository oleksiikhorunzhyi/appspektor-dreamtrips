package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedListWidthCommand;

import io.techery.janet.ActionPipe;

public class FeedListWidthInteractor {

   private ActionPipe<FeedListWidthCommand> feedListWidthPipe;

   public FeedListWidthInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      feedListWidthPipe = sessionActionPipeCreator.createPipe(FeedListWidthCommand.class);
   }

   public ActionPipe<FeedListWidthCommand> feedListWidthPipe() {
      return feedListWidthPipe;
   }
}
