package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.wallet.service.command.RecordListCommand;

import java.util.List;

public class WalletRecordsActionStorage implements ActionStorage<List<Record>> {

   private final RecordsStorage persistentRecordsStorage;

   public WalletRecordsActionStorage(RecordsStorage persistentRecordsStorage) {
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