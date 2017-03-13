package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;

import rx.Observable;

public class HashtagFeedStorageDelegate extends BaseFeedStorageDelegate<HashtagFeedStorageCommand> {

   private HashtagInteractor hashtagInteractor;
   private String hashtag;

   public HashtagFeedStorageDelegate(HashtagFeedStorageInteractor hashtagFeedStorageInteractor,
         HashtagInteractor hashtagInteractor, PostsInteractor postsInteractor,
         TripImagesInteractor tripImagesInteractor, BucketInteractor bucketInteractor,
         SessionHolder<UserSession> sessionHolder) {
      super(hashtagFeedStorageInteractor, postsInteractor, tripImagesInteractor, bucketInteractor, sessionHolder);
      this.hashtagInteractor = hashtagInteractor;
   }

   @Override
   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(super.getListStorageOperationObservable(),

            hashtagInteractor.getRefreshFeedsByHashtagsPipe()
                  .observeSuccess()
                  .map(refresh -> refresh.getResult())
                  .map(ListStorageOperationFactory::refreshItemsOperation),

            hashtagInteractor.getLoadNextFeedsByHashtagsPipe()
                  .observeSuccess()
                  .map(loadNext -> loadNext.getResult())
                  .map(ListStorageOperationFactory::addItemsOperation));
   }

   public void setHashtag(String hashtag) {
      this.hashtag = hashtag;
   }

   @Override
   protected HashtagFeedStorageCommand createCommand(ListStorageOperation listStorageOperation) {
      return new HashtagFeedStorageCommand(hashtag, listStorageOperation);
   }
}


