package com.worldventures.dreamtrips.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;

public class SmartCardFirmwareActionStorage implements ActionStorage<SmartCardFirmware> {

   private final WalletStorage walletStorage;

   public SmartCardFirmwareActionStorage(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SmartCardFirmwareCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardFirmware data) {
      walletStorage.saveSmartCardFirmware(data);
   }

   @Override
   public SmartCardFirmware get(@Nullable CacheBundle action) {
      return walletStorage.getSmartCardFirmware();
   }
}