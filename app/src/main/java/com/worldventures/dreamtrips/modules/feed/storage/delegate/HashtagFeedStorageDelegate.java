package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.HashtagFeedStorageInteractor;

import javax.inject.Inject;

import rx.Observable;

public class HashtagFeedStorageDelegate extends BaseFeedStorageDelegate<HashtagFeedStorageCommand> {

   @Inject HashtagInteractor hashtagInteractor;

   private String hashtag;

   public HashtagFeedStorageDelegate(HashtagFeedStorageInteractor hashtagFeedStorageInteractor, Injector injector) {
      super(hashtagFeedStorageInteractor, injector);
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


