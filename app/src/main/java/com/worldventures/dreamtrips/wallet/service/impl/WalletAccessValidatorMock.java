package com.worldventures.dreamtrips.wallet.service.impl;


import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.service.WalletAccessValidator;

import rx.functions.Action0;

public class WalletAccessValidatorMock implements WalletAccessValidator {

   @Override
   public void validate(@NonNull Action0 onAvailable, @NonNull Action0 onMissing) {
      onAvailable.call();
   }
}
