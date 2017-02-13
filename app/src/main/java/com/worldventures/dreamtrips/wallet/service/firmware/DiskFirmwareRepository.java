package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

class DiskFirmwareRepository implements FirmwareRepository {

   private final SnappyRepository snappyRepository;

   DiskFirmwareRepository(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public synchronized FirmwareUpdateData getFirmwareUpdateData() {
      return snappyRepository.getFirmwareUpdateData();
   }

   @Override
   public synchronized void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData) {
      snappyRepository.saveFirmwareUpdateData(firmwareUpdateData);
   }

   @Override
   public synchronized void clear() {
      snappyRepository.deleteFirmwareUpdateData();
   }
}
