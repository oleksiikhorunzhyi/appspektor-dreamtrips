package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;

public class SmartCardUserActionStorage implements ActionStorage<SmartCardUser>{

   private final SnappyRepository snappyRepository;

   public SmartCardUserActionStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardUser data) {
      snappyRepository.saveSmartCardUser(data);
   }

   @Override
   public SmartCardUser get(@Nullable CacheBundle action) {
      return snappyRepository.getSmartCardUser();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SmartCardUserCommand.class;
   }
}
