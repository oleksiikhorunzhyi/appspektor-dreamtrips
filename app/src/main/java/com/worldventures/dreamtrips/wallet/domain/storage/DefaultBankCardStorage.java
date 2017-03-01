package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentWalletCardsStorage;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;

public class DefaultBankCardStorage implements ActionStorage<String> {

   private final PersistentWalletCardsStorage cardsStorage;

   public DefaultBankCardStorage(PersistentWalletCardsStorage cardsStorage) {
      this.cardsStorage = cardsStorage;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, String defaultCardId) {
      cardsStorage.saveWalletDefaultCardId(defaultCardId);
   }

   @Override
   public synchronized String get(@Nullable CacheBundle bundle) {
      return cardsStorage.readWalletDefaultCardId();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DefaultCardIdCommand.class;
   }
}
