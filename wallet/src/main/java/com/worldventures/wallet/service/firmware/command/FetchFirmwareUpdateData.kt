package com.worldventures.wallet.service.firmware.command


import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.FirmwareUpdateData
import com.worldventures.wallet.service.firmware.FirmwareRepository
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class FetchFirmwareUpdateData : Command<FetchFirmwareUpdateData.Result>(), InjectableAction {

   @Inject lateinit var firmwareRepository: FirmwareRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Result>) {
      val firmwareUpdateData = firmwareRepository.firmwareUpdateData
      callback.onSuccess(Result(
            isForceUpdateStarted = firmwareUpdateData != null && firmwareUpdateData.isStarted && firmwareUpdateData.isFactoryResetRequired,
            firmwareUpdateData = firmwareUpdateData))
   }

   data class Result(
      val isForceUpdateStarted: Boolean,
      val firmwareUpdateData: FirmwareUpdateData? = null) {

      @Deprecated("Use isForceUpdateStarted")
      fun hasUpdate() = isForceUpdateStarted
   }
}
