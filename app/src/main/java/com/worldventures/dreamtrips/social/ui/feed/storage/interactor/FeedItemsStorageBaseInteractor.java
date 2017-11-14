package com.worldventures.dreamtrips.social.ui.feed.storage.interactor;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.FeedItemsStorageBaseCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public abstract class FeedItemsStorageBaseInteractor<C extends FeedItemsStorageBaseCommand> {

   private final ActionPipe<C> feedItemsStoragePipe;

   public FeedItemsStorageBaseInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      feedItemsStoragePipe = sessionActionPipeCreator.createPipe(getCommandClass(), Schedulers.io());
   }

   public ActionPipe<C> getFeedItemsStoragePipe() {
      return feedItemsStoragePipe;
   }

   protected abstract Class<C> getCommandClass();
}
