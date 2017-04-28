package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;

import javax.inject.Inject;

public class SmartCardDetailsActionStorage implements ActionStorage<SmartCardDetails> {

   private final SnappyRepository snappyRepository;

   @Inject
   public SmartCardDetailsActionStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return AssociateCardUserCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardDetails data) {
      snappyRepository.saveSmartCardDetails(data);
   }

   @Override
   public SmartCardDetails get(@Nullable CacheBundle params) {
      return snappyRepository.getSmartCardDetails();
   }
}

