package com.worldventures.dreamtrips.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;

import javax.inject.Inject;

public class TermsAndConditionsActionStorage implements ActionStorage<TermsAndConditions> {

   private final WalletStorage walletStorage;

   @Inject
   public TermsAndConditionsActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FetchTermsAndConditionsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, TermsAndConditions data) {
      walletStorage.saveWalletTermsAndConditions(data);
   }

   @Override
   public TermsAndConditions get(@Nullable CacheBundle action) {
      return walletStorage.getWalletTermsAndConditions();
   }
}