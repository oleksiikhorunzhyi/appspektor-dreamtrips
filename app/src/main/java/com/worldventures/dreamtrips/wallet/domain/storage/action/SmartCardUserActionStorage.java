package com.worldventures.dreamtrips.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;

public class SmartCardUserActionStorage implements ActionStorage<SmartCardUser> {

   private final WalletStorage walletStorage;

   public SmartCardUserActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardUser data) {
      walletStorage.saveSmartCardUser(data);
   }

   @Override
   public SmartCardUser get(@Nullable CacheBundle action) {
      return walletStorage.getSmartCardUser();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SmartCardUserCommand.class;
   }
}
