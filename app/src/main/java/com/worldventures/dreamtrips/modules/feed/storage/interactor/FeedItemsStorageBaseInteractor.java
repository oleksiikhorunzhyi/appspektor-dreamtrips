package com.worldventures.dreamtrips.modules.feed.storage.interactor;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedItemsStorageBaseCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public abstract class FeedItemsStorageBaseInteractor<C extends FeedItemsStorageBaseCommand>  {

   private ActionPipe<C> feedItemsStoragePipe;

   public FeedItemsStorageBaseInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      feedItemsStoragePipe = sessionActionPipeCreator.createPipe(getCommandClass(), Schedulers.io());
   }

   public ActionPipe<C> getFeedItemsStoragePipe() {
      return feedItemsStoragePipe;
   }

   protected abstract Class<C> getCommandClass();
}
