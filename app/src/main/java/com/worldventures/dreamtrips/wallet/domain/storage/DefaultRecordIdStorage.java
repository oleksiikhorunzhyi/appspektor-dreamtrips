package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

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
