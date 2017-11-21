package com.worldventures.wallet.service.firmware.command

import com.worldventures.wallet.domain.entity.SmartCardFirmware
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.support.UpgradeAppFirmwareAction
import io.techery.janet.smartcard.event.UpgradeAppFirmwareProgressEvent
import rx.Observable
import java.io.File

@CommandAction
class LoadNordicFirmwareCommand(private val firmwareFile: File, private val firmwareVersion: String, private val bootloaderFile: Boolean) : BaseLoadFirmwareCommand() {

   internal override fun provideProgress(): Observable<Int> {
      return janet.createPipe(UpgradeAppFirmwareProgressEvent::class.java)
            .observeSuccess()
            .map { event -> event.progress }
   }

   internal override fun loadFile(): Observable<Void> {
      return janet.createPipe(UpgradeAppFirmwareAction::class.java)
            .createObservableResult(UpgradeAppFirmwareAction(firmwareFile))
            .map { action -> null }
   }

   internal override fun updatedSmartCardFirmware(currentSmartCardFirmware: SmartCardFirmware): SmartCardFirmware {
      return if (bootloaderFile)
         currentSmartCardFirmware.copy(nrfBootloaderVersion = firmwareVersion)
      else
         currentSmartCardFirmware.copy(nordicAppVersion = firmwareVersion)
   }

}

