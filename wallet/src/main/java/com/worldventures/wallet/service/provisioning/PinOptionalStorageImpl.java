package com.worldventures.wallet.service.provisioning;


import com.worldventures.wallet.domain.storage.WalletStorage;

public class PinOptionalStorageImpl implements PinOptionalStorage {

   private final WalletStorage walletStorage;

   public PinOptionalStorageImpl(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public boolean shouldAskForPin() {
      return walletStorage.shouldAskForPin();
   }

   @Override
   public void saveShouldAskForPin(boolean shouldAskForPin) {
      walletStorage.saveShouldAskForPin(shouldAskForPin);
   }
}
