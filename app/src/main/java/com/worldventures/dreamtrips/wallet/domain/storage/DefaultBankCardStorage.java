package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;

public class DefaultBankCardStorage implements ActionStorage<String> {

   private final SnappyRepository snappyRepository;

   public DefaultBankCardStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, String defaultCardId) {
      snappyRepository.saveWalletDefaultCardId(defaultCardId);
   }

   @Override
   public synchronized String get(@Nullable CacheBundle bundle) {
      return snappyRepository.readWalletDefaultCardId();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DefaultCardIdCommand.class;
   }
}
