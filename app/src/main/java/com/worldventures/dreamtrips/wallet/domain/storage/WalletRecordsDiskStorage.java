package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;

import java.util.List;

public class WalletRecordsDiskStorage implements ActionStorage<List<Record>> {

   private final RecordsStorage persistentRecordsStorage;

   public WalletRecordsDiskStorage(RecordsStorage persistentRecordsStorage) {
      this.persistentRecordsStorage = persistentRecordsStorage;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, List<Record> data) {
      persistentRecordsStorage.saveRecords(data);
   }

   @Override
   public synchronized List<Record> get(@Nullable CacheBundle bundle) {
      return persistentRecordsStorage.readRecords();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return RecordListCommand.class;
   }

}