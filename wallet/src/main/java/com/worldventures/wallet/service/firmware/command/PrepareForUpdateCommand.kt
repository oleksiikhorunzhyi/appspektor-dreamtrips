package com.worldventures.wallet.service.firmware.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.service.command.FactoryResetCommand
import com.worldventures.wallet.service.command.reset.ResetOptions
import com.worldventures.wallet.service.firmware.FirmwareRepository
import com.worldventures.wallet.service.firmware.FirmwareUpdateType
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class PrepareForUpdateCommand : Command<FirmwareUpdateType>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var firmwareRepository: FirmwareRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<FirmwareUpdateType>) {
      callback.onProgress(0)
      val updateData = firmwareRepository.firmwareUpdateData.copy(isStarted = true)
      firmwareRepository.firmwareUpdateData = updateData

      if (!updateData.isFactoryResetRequired) {
         callback.onSuccess(FirmwareUpdateType.NORMAL)
      } else {
         janet.createPipe(FactoryResetCommand::class.java)
               .createObservableResult(FactoryResetCommand(ResetOptions.builder().build()))
               .subscribe({ callback.onSuccess(FirmwareUpdateType.CRITICAL) }, { callback.onFail(it) })
      }
   }
}
