package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentRecordsStorage;
import com.worldventures.dreamtrips.wallet.service.command.DefaultRecordIdCommand;

public class DefaultRecordIdStorage implements ActionStorage<String> {

   private final PersistentRecordsStorage cardsStorage;

   public DefaultRecordIdStorage(PersistentRecordsStorage cardsStorage) {
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
