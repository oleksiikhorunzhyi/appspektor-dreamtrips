package com.worldventures.dreamtrips.social.ui.feed.storage.interactor;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.HashtagFeedStorageCommand;

public class HashtagFeedStorageInteractor extends FeedItemsStorageBaseInteractor<HashtagFeedStorageCommand> {

   public HashtagFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<HashtagFeedStorageCommand> getCommandClass() {
      return HashtagFeedStorageCommand.class;
   }
}


