package com.worldventures.dreamtrips.modules.feed.storage.interactor;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.storage.command.UserTimelineStorageCommand;

public class UserTimelineStorageInteractor extends FeedItemsStorageBaseInteractor<UserTimelineStorageCommand> {

   public UserTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<UserTimelineStorageCommand> getCommandClass() {
      return UserTimelineStorageCommand.class;
   }
}
