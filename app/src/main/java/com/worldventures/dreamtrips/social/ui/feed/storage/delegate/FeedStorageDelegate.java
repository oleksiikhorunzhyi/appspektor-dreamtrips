package com.worldventures.dreamtrips.social.ui.feed.storage.delegate;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.AddItemsStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.RefreshStorageOperation;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.FeedStorageCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class FeedStorageDelegate extends BaseFeedStorageDelegate<FeedStorageCommand> {

   public FeedStorageDelegate(FeedStorageInteractor feedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      super(feedStorageInteractor, feedInteractor, postsInteractor, tripImagesInteractor, bucketInteractor, friendsInteractor, commentsInteractor);
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            feedInteractor.getRefreshAccountFeedPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(RefreshStorageOperation::new),

            feedInteractor.getLoadNextAccountFeedPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .map(AddItemsStorageOperation::new));
   }

   @Override
   protected FeedStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new FeedStorageCommand(listStorageOperation);
   }
}
