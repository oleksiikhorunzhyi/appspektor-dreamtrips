package com.worldventures.dreamtrips.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;

public class SyncRecordsStatusActionStorage implements ActionStorage<SyncRecordsStatus> {

   private final WalletStorage walletStorage;

   public SyncRecordsStatusActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, SyncRecordsStatus data) {
      walletStorage.saveSyncRecordsStatus(data);
   }

   @Override
   public SyncRecordsStatus get(@Nullable CacheBundle action) {
      return walletStorage.getSyncRecordsStatus();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SyncRecordStatusCommand.class;
   }
}
