package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;

class DiskFirmwareRepository implements FirmwareRepository {

   private final WalletStorage walletStorage;

   DiskFirmwareRepository(WalletStorage walletStorage) {
      this.walletStorage = walletStorage;
   }

   @Override
   public synchronized FirmwareUpdateData getFirmwareUpdateData() {
      return walletStorage.getFirmwareUpdateData();
   }

   @Override
   public synchronized void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData) {
      walletStorage.saveFirmwareUpdateData(firmwareUpdateData);
   }

   @Override
   public synchronized void clear() {
      walletStorage.deleteFirmwareUpdateData();
   }
}
