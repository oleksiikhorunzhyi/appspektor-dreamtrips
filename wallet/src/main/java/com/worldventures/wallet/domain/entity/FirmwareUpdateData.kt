package com.worldventures.wallet.domain.entity

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo
import java.io.File
import java.io.Serializable

data class FirmwareUpdateData(
      val isUpdateAvailable: Boolean,
      val isUpdateCritical: Boolean,
      val isFactoryResetRequired: Boolean,
      val smartCardId: String,
      val currentFirmwareVersion: SmartCardFirmware,
      val firmwareInfo: FirmwareInfo? = null,
      val firmwareFile: File? = null,
      val isStarted: Boolean = false
) : Serializable {

   val isFileDownloaded: Boolean
      get() = firmwareFile != null
}
