package com.worldventures.wallet.service.impl;

import android.support.annotation.NonNull;

import com.worldventures.core.model.session.Feature;
import com.worldventures.core.model.session.FeatureManager;
import com.worldventures.wallet.service.WalletAccessValidator;

import rx.functions.Action0;

public class WalletAccessValidatorImpl implements WalletAccessValidator {

   private final FeatureManager featureManager;

   public WalletAccessValidatorImpl(FeatureManager featureManager) {
      this.featureManager = featureManager;
   }

   @Override
   public void validate(@NonNull Action0 onAvailable, @NonNull Action0 onMissing) {
      featureManager.with(Feature.WALLET_PROVISIONING, onAvailable::call, onMissing::call);
   }
}
