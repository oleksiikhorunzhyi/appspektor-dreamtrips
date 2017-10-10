package com.worldventures.dreamtrips.social.ui.feed.storage.delegate;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.AddItemsStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.RefreshStorageOperation;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;

import rx.Observable;

public class HashtagFeedStorageDelegate extends BaseFeedStorageDelegate<HashtagFeedStorageCommand> {

   private HashtagInteractor hashtagInteractor;

   private String hashtag;

   public HashtagFeedStorageDelegate(HashtagInteractor hashtagInteractor, HashtagFeedStorageInteractor hashtagFeedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      super(hashtagFeedStorageInteractor, feedInteractor, postsInteractor, tripsInteractor, tripImagesInteractor,
            bucketInteractor, friendsInteractor, commentsInteractor);
      this.hashtagInteractor = hashtagInteractor;
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            hashtagInteractor.getRefreshFeedsByHashtagsPipe()
                  .observeSuccess()
                  .map(refresh -> refresh.getResult())
                  .map(RefreshStorageOperation::new),

            hashtagInteractor.getLoadNextFeedsByHashtagsPipe()
                  .observeSuccess()
                  .map(loadNext -> loadNext.getResult())
                  .map(AddItemsStorageOperation::new));
   }

   public void setHashtag(String hashtag) {
      this.hashtag = hashtag;
   }

   @Override
   protected HashtagFeedStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new HashtagFeedStorageCommand(hashtag, listStorageOperation);
   }
}


