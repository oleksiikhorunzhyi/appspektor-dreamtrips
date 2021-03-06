package com.worldventures.wallet.service.command.record;

import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class SyncRecordStatusCommand extends CachedValueCommand<SyncRecordsStatus> {

   private SyncRecordStatusCommand(Func1<SyncRecordsStatus, SyncRecordsStatus> func) {
      super(func);
   }

   public static SyncRecordStatusCommand fetch() {
      return new SyncRecordStatusCommand(syncRecordsStatus -> {
         if (syncRecordsStatus == null) {
            return SyncRecordsStatus.SUCCESS;
         }
         return syncRecordsStatus;
      });
   }

   public static SyncRecordStatusCommand save(SyncRecordsStatus status) {
      return new SyncRecordStatusCommand(syncRecordsStatus -> status);
   }
}
