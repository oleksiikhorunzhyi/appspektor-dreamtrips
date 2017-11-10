package com.worldventures.wallet.service.firmware.command

import com.worldventures.wallet.domain.entity.SmartCardFirmware
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.support.UpgradeExtAtmelFirmwareAction
import io.techery.janet.smartcard.event.UpgradeExtAtmelFirmwareProgressEvent
import rx.Observable
import java.io.File

@CommandAction
class LoadPuckAtmelFirmwareCommand(private val firmwareFile: File, private val firmwareVersion: String) : BaseLoadFirmwareCommand() {

   internal override fun provideProgress(): Observable<Int> {
      return janet.createPipe(UpgradeExtAtmelFirmwareProgressEvent::class.java)
            .observeSuccess()
            .map { event -> event.progress }
   }

   internal override fun loadFile(): Observable<Void> {
      return janet.createPipe(UpgradeExtAtmelFirmwareAction::class.java)
            .createObservableResult(UpgradeExtAtmelFirmwareAction(firmwareFile))
            .map { action -> null as Void? }
   }

   internal override fun updatedSmartCardFirmware(currentSmartCardFirmware: SmartCardFirmware): SmartCardFirmware {
      return currentSmartCardFirmware.copy(externalAtmelVersion = firmwareVersion)
   }
}
