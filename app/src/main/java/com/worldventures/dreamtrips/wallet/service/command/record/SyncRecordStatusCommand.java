package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class SyncRecordStatusCommand extends CachedValueCommand<SyncRecordsStatus> {

   private SyncRecordStatusCommand(Func1<SyncRecordsStatus, SyncRecordsStatus> func) {
      super(func);
   }

   public static SyncRecordStatusCommand fetch() {
      return new SyncRecordStatusCommand(syncRecordsStatus -> syncRecordsStatus);
   }

   public static SyncRecordStatusCommand save(SyncRecordsStatus status) {
      return new SyncRecordStatusCommand(syncRecordsStatus -> status);
   }
}
