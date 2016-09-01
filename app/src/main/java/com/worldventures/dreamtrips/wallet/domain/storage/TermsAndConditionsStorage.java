package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditionsResponse;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;

import javax.inject.Inject;

public class TermsAndConditionsStorage implements ActionStorage<TermsAndConditionsResponse> {

   private final SnappyRepository snappyRepository;

   @Inject
   public TermsAndConditionsStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FetchTermsAndConditionsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, TermsAndConditionsResponse data) {
      snappyRepository.saveWalletTermsAndConditions(data);
   }

   @Override
   public TermsAndConditionsResponse get(@Nullable CacheBundle action) {
      return snappyRepository.getWalletTermsAndConditions();
   }
}