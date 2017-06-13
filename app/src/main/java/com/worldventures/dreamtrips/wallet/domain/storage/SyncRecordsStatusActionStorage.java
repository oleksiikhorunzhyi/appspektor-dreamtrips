package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;

public class SyncRecordsStatusActionStorage implements ActionStorage<SyncRecordsStatus> {

   private final SnappyRepository snappyRepository;

   public SyncRecordsStatusActionStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, SyncRecordsStatus data) {
      snappyRepository.saveSyncRecordsStatus(data);
   }

   @Override
   public SyncRecordsStatus get(@Nullable CacheBundle action) {
      return snappyRepository.getSyncRecordsStatus();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SyncRecordStatusCommand.class;
   }
}
