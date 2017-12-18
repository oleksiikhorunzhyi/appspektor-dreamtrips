package com.worldventures.wallet.service.firmware.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.service.firmware.FirmwareRepository
import io.techery.janet.Command
import io.techery.janet.Janet
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

abstract class BaseLoadFirmwareCommand : Command<Void>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var firmwareRepository: FirmwareRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      val subscription = provideProgress().subscribe({ callback.onProgress(it) })

      loadFile()
            .flatMap { updateFirmware() }
            .subscribe({
               subscription.unsubscribe()
               callback.onSuccess(null)
            }, { callback.onFail(it) })
   }

   private fun updateFirmware(): Observable<Void> {
      val firmwareData = firmwareRepository.firmwareUpdateData
      val newFirmwareData = firmwareData.copy(currentFirmwareVersion = updatedSmartCardFirmware(firmwareData.currentFirmwareVersion))
      firmwareRepository.firmwareUpdateData = newFirmwareData
      return Observable.just(null)
   }

   internal abstract fun provideProgress(): Observable<Int>

   internal abstract fun loadFile(): Observable<Void>

   internal abstract fun updatedSmartCardFirmware(currentSmartCardFirmware: SmartCardFirmware): SmartCardFirmware
}
