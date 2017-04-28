package com.worldventures.dreamtrips.wallet.service.firmware;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

public interface FirmwareRepository {

   FirmwareUpdateData getFirmwareUpdateData();

   void setFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData);

   void clear();
}
