package com.worldventures.wallet.service.firmware.command

import android.content.Context
import com.worldventures.core.service.command.DownloadFileCommand
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.service.firmware.FirmwareRepository
import com.worldventures.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class DownloadFirmwareCommand : Command<Void>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var firmwareRepository: FirmwareRepository
   @Inject lateinit var appContext: Context // todo: remove from command

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      val firmwareUpdateData = firmwareRepository.firmwareUpdateData
      val firmwareInfo = firmwareUpdateData.firmwareInfo ?: throw IllegalStateException("Firmware is not available")

      janet.createPipe(DownloadFileCommand::class.java)
            .createObservableResult(DownloadFileCommand(getAppropriateFirmwareFile(appContext), firmwareInfo.url()))
            .map { firmwareUpdateData.copy(firmwareFile = it.result) }
            .subscribe({ result ->
               firmwareRepository.firmwareUpdateData = result
               callback.onSuccess(null)
            },  { callback.onFail(it) })
   }
}
