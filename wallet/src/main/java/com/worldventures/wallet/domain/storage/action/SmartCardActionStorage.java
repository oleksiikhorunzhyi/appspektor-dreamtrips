package com.worldventures.wallet.domain.storage.action;


import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;

public class SmartCardActionStorage implements ActionStorage<SmartCard> {

   private final WalletStorage walletStorage;

   public SmartCardActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCard data) {
      if (data != null) {
         walletStorage.saveSmartCard(data);
      }
   }

   @Override
   public SmartCard get(@Nullable CacheBundle bundle) {
      return walletStorage.getSmartCard();
   }


   @Override
   public Class<? extends CachedAction> getActionClass() {
      return ActiveSmartCardCommand.class;
   }
}
