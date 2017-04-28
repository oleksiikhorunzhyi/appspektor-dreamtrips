package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.storage.command.UserTimelineStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.UserTimelineStorageInteractor;

import io.techery.janet.Command;
import rx.Observable;

public class UserTimelineStorageDelegate extends BaseFeedStorageDelegate<UserTimelineStorageCommand> {
   private int userId;

   public UserTimelineStorageDelegate(UserTimelineStorageInteractor userTimelineStorageInteractor, Injector injector) {
      super(userTimelineStorageInteractor, injector);
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
