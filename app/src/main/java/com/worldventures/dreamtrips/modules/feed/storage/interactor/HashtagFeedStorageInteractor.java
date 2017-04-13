package com.worldventures.dreamtrips.modules.feed.storage.interactor;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.storage.command.HashtagFeedStorageCommand;

public class HashtagFeedStorageInteractor extends FeedItemsStorageBaseInteractor<HashtagFeedStorageCommand> {

   public HashtagFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<HashtagFeedStorageCommand> getCommandClass() {
      return HashtagFeedStorageCommand.class;
   }
}


