package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.command.http.AssociateCardUserCommand;

import javax.inject.Inject;

public class SmartCardDetailsActionStorage implements ActionStorage<SmartCardDetails> {

   private final WalletStorage walletStorage;

   @Inject
   public SmartCardDetailsActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return AssociateCardUserCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardDetails data) {
      walletStorage.saveSmartCardDetails(data);
   }

   @Override
   public SmartCardDetails get(@Nullable CacheBundle params) {
      return walletStorage.getSmartCardDetails();
   }
}

