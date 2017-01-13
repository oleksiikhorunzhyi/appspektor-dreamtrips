package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

class DiskFirmwareRepository implements FirmwareRepository {

   private final SnappyRepository snappyRepository;

   private FirmwareUpdateData firmwareUpdateData;

   DiskFirmwareRepository(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public FirmwareUpdateData getFirmwareUpdateData() {
      synchronized (this) {
         if (firmwareUpdateData == null) {
            firmwareUpdateData = null;
         }
      }
      return firmwareUpdateData;
   }

   @Override
   public synchronized void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData;
   }

   public synchronized void saveOnDisk() {
      if (firmwareUpdateData == null) return;
      snappyRepository.saveFirmwareUpdateData(firmwareUpdateData);
   }

   @Override
   public synchronized void clear() {
      snappyRepository.deleteFirmwareUpdateData();
   }
}
