package com.worldventures.dreamtrips.social.ui.feed.storage.interactor;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.AccountTimelineStorageCommand;

public class AccountTimelineStorageInteractor extends FeedItemsStorageBaseInteractor<AccountTimelineStorageCommand> {

   public AccountTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      super(sessionActionPipeCreator);
   }

   @Override
   protected Class<AccountTimelineStorageCommand> getCommandClass() {
      return AccountTimelineStorageCommand.class;
   }
}
