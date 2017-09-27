package com.worldventures.dreamtrips.wallet.service.impl;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.service.WalletAccessValidator;

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
