package com.worldventures.dreamtrips.wallet.service.impl;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.service.StorageNameProvider;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;

public class StorageNameProviderImpl implements StorageNameProvider {

   private final WalletSocialInfoProvider socialInfoProvider;

   public StorageNameProviderImpl(WalletSocialInfoProvider socialInfoProvider) {
      this.socialInfoProvider = socialInfoProvider;
   }

   @Nullable
   @Override
   public String folderName() {
      return socialInfoProvider.hasUser() ? String.valueOf(socialInfoProvider.userId().toString().hashCode()) : null;
   }
}
