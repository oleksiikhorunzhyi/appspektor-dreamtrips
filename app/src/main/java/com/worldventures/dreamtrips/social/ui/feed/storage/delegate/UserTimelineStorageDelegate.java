package com.worldventures.dreamtrips.social.ui.feed.storage.delegate;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.UserTimelineStorageCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class UserTimelineStorageDelegate extends BaseFeedStorageDelegate<UserTimelineStorageCommand> {
   private int userId;

   public UserTimelineStorageDelegate(UserTimelineStorageInteractor userTimelineStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      super(userTimelineStorageInteractor, feedInteractor, postsInteractor, tripsInteractor,
            tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            feedInteractor.getRefreshUserTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::refreshItemsOperation),

            feedInteractor.getLoadNextUserTimelinePipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(ListStorageOperationFactory::addItemsOperation));
   }

   public void setUserId(int userId) {
      this.userId = userId;
   }

   @Override
   protected UserTimelineStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new UserTimelineStorageCommand(userId, listStorageOperation);
   }
}