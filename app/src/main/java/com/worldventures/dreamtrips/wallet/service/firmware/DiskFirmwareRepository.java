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
            firmwareUpdateData = snappyRepository.getFirmwareUpdateData();
         }
      }
      return firmwareUpdateData;
   }

   @Override
   public synchronized void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData;
      snappyRepository.saveFirmwareUpdateData(firmwareUpdateData);
   }

   @Override
   public synchronized void clear() {
      snappyRepository.deleteFirmwareUpdateData();
      firmwareUpdateData = null;
   }
}
