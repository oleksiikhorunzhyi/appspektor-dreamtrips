package com.worldventures.wallet.service.firmware.command

import com.worldventures.wallet.domain.entity.SmartCardFirmware
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.support.UpgradeIntAtmelFirmwareAction
import io.techery.janet.smartcard.event.UpgradeIntAtmelFirmwareProgressEvent
import rx.Observable
import java.io.File

@CommandAction
class LoadAppAtmelFirmwareCommand(private val firmwareFile: File, private val firmwareVersion: String) : BaseLoadFirmwareCommand() {

   override fun provideProgress(): Observable<Int> {
      return janet.createPipe(UpgradeIntAtmelFirmwareProgressEvent::class.java)
            .observeSuccess()
            .map { event -> event.progress }
   }

   override fun loadFile(): Observable<Void> {
      return janet.createPipe(UpgradeIntAtmelFirmwareAction::class.java)
            .createObservableResult(UpgradeIntAtmelFirmwareAction(firmwareFile))
            .map { null }
   }

   override fun updatedSmartCardFirmware(currentSmartCardFirmware: SmartCardFirmware): SmartCardFirmware {
      return currentSmartCardFirmware.copy(internalAtmelVersion = firmwareVersion)
   }

}
