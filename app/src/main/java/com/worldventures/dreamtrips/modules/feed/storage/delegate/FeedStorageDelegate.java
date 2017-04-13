package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedStorageInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class FeedStorageDelegate extends BaseFeedStorageDelegate<FeedStorageCommand> {

   public FeedStorageDelegate(FeedStorageInteractor feedStorageInteractor, Injector injector) {
      super(feedStorageInteractor, injector);
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
