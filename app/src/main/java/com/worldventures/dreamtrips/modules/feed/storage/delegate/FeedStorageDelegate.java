package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class FeedStorageDelegate extends BaseFeedStorageDelegate<FeedStorageCommand> {
   private FeedInteractor feedInteractor;

   public FeedStorageDelegate(FeedStorageInteractor feedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, SessionHolder<UserSession> sessionHolder) {
      super(feedStorageInteractor, postsInteractor, tripImagesInteractor, bucketInteractor, sessionHolder);
      this.feedInteractor = feedInteractor;
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            feedInteractor.getRefreshAccountFeedPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::refreshItemsOperation),

            feedInteractor.getLoadNextAccountFeedPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::addItemsOperation));
   }

   @Override
   protected FeedStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new FeedStorageCommand(listStorageOperation);
   }
}
