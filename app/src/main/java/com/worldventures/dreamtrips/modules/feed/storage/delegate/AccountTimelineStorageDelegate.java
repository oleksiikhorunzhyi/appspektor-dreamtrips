package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.command.AccountTimelineStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class AccountTimelineStorageDelegate extends BaseFeedStorageDelegate<AccountTimelineStorageCommand> {
   private FeedInteractor feedInteractor;

   public AccountTimelineStorageDelegate(AccountTimelineStorageInteractor accountTimelineStorageInteractor,
         FeedInteractor feedInteractor, PostsInteractor postsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, SessionHolder<UserSession> sessionHolder) {
      super(accountTimelineStorageInteractor, postsInteractor, tripImagesInteractor, bucketInteractor, sessionHolder);
      this.feedInteractor = feedInteractor;
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            feedInteractor.getRefreshAccountTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::refreshItemsOperation),

            feedInteractor.getLoadNextAccountTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::addItemsOperation));
   }

   @Override
   protected AccountTimelineStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new AccountTimelineStorageCommand(listStorageOperation);
   }
}
