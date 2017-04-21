package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware
import java.io.File

class TestFirmwareUpdateData(
      private val smartCardId: String,
      private val updateAvailable: Boolean,
      private val updateCritical: Boolean,
      private val factoryResetRequired: Boolean,
      private val currentFirmwareVersion: SmartCardFirmware,
      private val firmwareInfo: FirmwareInfo? = TestFirmwareInfo(),
      private val firmwareFile: File? = null
): FirmwareUpdateData() {

   override fun updateAvailable() = updateAvailable

   override fun updateCritical() = updateCritical

   override fun factoryResetRequired() = factoryResetRequired

   override fun smartCardId() = smartCardId

   override fun currentFirmwareVersion() = currentFirmwareVersion

   override fun firmwareInfo() = firmwareInfo

   override fun firmwareFile() = firmwareFile
}