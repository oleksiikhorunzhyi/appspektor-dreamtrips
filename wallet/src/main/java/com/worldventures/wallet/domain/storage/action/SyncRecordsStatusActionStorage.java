package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.command.record.SyncRecordStatusCommand;

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
