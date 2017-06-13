package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SyncRecordOnNewDeviceCommand extends Command<SyncRecordsStatus> implements InjectableAction {

   @Inject RecordInteractor interactor;

   @Override
   protected void run(CommandCallback<SyncRecordsStatus> callback) throws Throwable {
      interactor.recordsSyncPipe()
            .createObservableResult(new SyncRecordsCommand())
            .subscribe(command -> onSuccess(callback), throwable -> onFail(callback, throwable));
   }

   private void onSuccess(CommandCallback<SyncRecordsStatus> callback) {
      interactor.syncRecordStatusPipe().send(SyncRecordStatusCommand.save(SyncRecordsStatus.SUCCESS));
      callback.onSuccess(SyncRecordsStatus.FAIL_AFTER_PROVISION);
   }

   private void onFail(CommandCallback<SyncRecordsStatus> callback, Throwable throwable) {
      interactor.syncRecordStatusPipe().send(SyncRecordStatusCommand.save(SyncRecordsStatus.FAIL_AFTER_PROVISION));
      callback.onFail(throwable);
   }
}
