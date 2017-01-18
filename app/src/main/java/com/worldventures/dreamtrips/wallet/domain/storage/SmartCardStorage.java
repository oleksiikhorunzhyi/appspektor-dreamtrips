package com.worldventures.dreamtrips.wallet.domain.storage;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

public class SmartCardStorage implements ActionStorage<SmartCard> {

   private final SnappyRepository snappyRepository;

   public SmartCardStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCard data) {
      if (data != null) {
         snappyRepository.saveSmartCard(data);
      }
   }

   @Override
   public SmartCard get(@Nullable CacheBundle bundle) {
      return snappyRepository.getSmartCard();
   }


   @Override
   public Class<? extends CachedAction> getActionClass() {
      return ActiveSmartCardCommand.class;
   }
}
