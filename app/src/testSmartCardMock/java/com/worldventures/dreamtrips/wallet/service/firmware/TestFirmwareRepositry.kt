package com.worldventures.dreamtrips.wallet.service.firmware

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData

class TestFirmwareRepositry(private var firmwareUpdateData: FirmwareUpdateData? = null) : FirmwareRepository {

   override fun getFirmwareUpdateData() = firmwareUpdateData

   override fun setFirmwareUpdateData(firmwareUpdateData: FirmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData
   }

   override fun clear() {
      this.firmwareUpdateData = null
   }
}