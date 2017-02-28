package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.command.UserTimelineStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class UserTimelineStorageDelegate extends BaseFeedStorageDelegate<UserTimelineStorageCommand> {
   private int userId;
   private FeedInteractor feedInteractor;

   public UserTimelineStorageDelegate(UserTimelineStorageInteractor userTimelineStorageInteractor,
         FeedInteractor feedInteractor, PostsInteractor postsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, SessionHolder<UserSession> sessionHolder) {
      super(userTimelineStorageInteractor, postsInteractor, tripImagesInteractor, bucketInteractor, sessionHolder);
      this.feedInteractor = feedInteractor;
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
