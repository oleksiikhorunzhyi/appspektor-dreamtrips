package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;

import java.util.Arrays;
import java.util.List;

public class DefaultBankCardStorage implements MultipleActionStorage<String> {

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
   public List<Class<? extends CachedAction>> getActionClasses() {
      return Arrays.asList(FetchDefaultCardCommand.class, SetDefaultCardOnDeviceCommand.class);
   }

}