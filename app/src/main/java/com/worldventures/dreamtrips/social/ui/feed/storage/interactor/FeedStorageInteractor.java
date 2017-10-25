package com.worldventures.dreamtrips.social.ui.feed.storage.interactor;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.FeedStorageCommand;

public class FeedStorageInteractor extends FeedItemsStorageBaseInteractor<FeedStorageCommand> {

   public FeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<FeedStorageCommand> getCommandClass() {
      return FeedStorageCommand.class;
   }
}
