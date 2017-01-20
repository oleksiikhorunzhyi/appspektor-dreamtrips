package com.worldventures.dreamtrips.modules.feed.storage.interactor;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand;

public class FeedStorageInteractor extends FeedItemsStorageBaseInteractor<FeedStorageCommand> {

   public FeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<FeedStorageCommand> getCommandClass() {
      return FeedStorageCommand.class;
   }
}
