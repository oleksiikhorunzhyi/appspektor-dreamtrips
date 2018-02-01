package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand;

public class DefaultRecordIdStorage implements ActionStorage<String> {

   private final RecordsStorage cardsStorage;

   public DefaultRecordIdStorage(RecordsStorage cardsStorage) {
      this.cardsStorage = cardsStorage;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, String defaultCardId) {
      cardsStorage.saveDefaultRecordId(defaultCardId);
   }

   @Override
   public synchronized String get(@Nullable CacheBundle bundle) {
      return cardsStorage.readDefaultRecordId();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DefaultRecordIdCommand.class;
   }
}
