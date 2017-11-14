package com.worldventures.wallet.service.firmware

import com.worldventures.wallet.domain.entity.FirmwareUpdateData

class TestFirmwareRepositry(private var firmwareUpdateData: FirmwareUpdateData? = null) : FirmwareRepository {

   override fun getFirmwareUpdateData() = firmwareUpdateData

   override fun setFirmwareUpdateData(firmwareUpdateData: FirmwareUpdateData) {
      this.firmwareUpdateData = firmwareUpdateData
   }

   override fun clear() {
      this.firmwareUpdateData = null
   }
}