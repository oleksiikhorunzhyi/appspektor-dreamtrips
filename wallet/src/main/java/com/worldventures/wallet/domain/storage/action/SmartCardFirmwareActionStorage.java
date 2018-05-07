package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.command.device.SmartCardFirmwareCommand;

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
