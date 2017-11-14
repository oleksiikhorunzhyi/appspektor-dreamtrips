package com.worldventures.wallet.service.firmware;

import com.worldventures.wallet.domain.entity.FirmwareUpdateData;

public interface FirmwareRepository {

   FirmwareUpdateData getFirmwareUpdateData();

   void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData);

   void clear();
}
