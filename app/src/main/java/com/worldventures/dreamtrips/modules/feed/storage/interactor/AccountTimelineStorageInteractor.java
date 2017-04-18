package com.worldventures.dreamtrips.modules.feed.storage.interactor;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.storage.command.AccountTimelineStorageCommand;

public class AccountTimelineStorageInteractor extends FeedItemsStorageBaseInteractor<AccountTimelineStorageCommand> {

   public AccountTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<AccountTimelineStorageCommand> getCommandClass() {
      return AccountTimelineStorageCommand.class;
   }
}
