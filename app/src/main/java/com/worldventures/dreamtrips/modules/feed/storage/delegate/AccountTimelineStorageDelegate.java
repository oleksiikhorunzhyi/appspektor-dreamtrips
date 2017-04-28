package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.storage.command.AccountTimelineStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.AccountTimelineStorageInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class AccountTimelineStorageDelegate extends BaseFeedStorageDelegate<AccountTimelineStorageCommand> {

   public AccountTimelineStorageDelegate(AccountTimelineStorageInteractor accountTimelineStorageInteractor,
         Injector injector) {
      super(accountTimelineStorageInteractor, injector);
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
