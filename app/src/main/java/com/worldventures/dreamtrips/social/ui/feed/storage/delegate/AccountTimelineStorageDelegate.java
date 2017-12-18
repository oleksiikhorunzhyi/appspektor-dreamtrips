package com.worldventures.dreamtrips.social.ui.feed.storage.delegate;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.AddItemsStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.RefreshStorageOperation;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.AccountTimelineStorageCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedItemsStorageBaseInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class AccountTimelineStorageDelegate extends BaseFeedStorageDelegate<AccountTimelineStorageCommand> {

   public AccountTimelineStorageDelegate(FeedItemsStorageBaseInteractor<? extends AccountTimelineStorageCommand> feedStorageInteractor,
         FeedInteractor feedInteractor, PostsInteractor postsInteractor, TripsInteractor tripsInteractor,
         TripImagesInteractor tripImagesInteractor, BucketInteractor bucketInteractor,
         FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      super(feedStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            feedInteractor.getRefreshAccountTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(RefreshStorageOperation::new),

            feedInteractor.getLoadNextAccountTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(AddItemsStorageOperation::new));
   }

   @Override
   protected AccountTimelineStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new AccountTimelineStorageCommand(listStorageOperation);
   }
}
