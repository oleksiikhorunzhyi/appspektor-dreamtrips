package com.worldventures.wallet.service;

import android.support.annotation.NonNull;

import rx.functions.Action0;

public interface WalletAccessValidator {

   void validate(@NonNull Action0 onAvailable, @NonNull Action0 onMissing);
}
