package com.worldventures.wallet.analytics.firmware

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.analytics.firmware.action.FirmwareAnalyticsAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.firmware.FirmwareRepository
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class WalletFirmwareAnalyticsCommand(private val action: FirmwareAnalyticsAction) : Command<Void>(), InjectableAction {

   @Inject lateinit var firmwareRepository: FirmwareRepository
   @Inject lateinit var analyticsInteractor: WalletAnalyticsInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      val data = firmwareRepository.firmwareUpdateData
      if (data != null) {
         action.setFirmwareData(data)
      }
      analyticsInteractor.analyticsActionPipe().send(action)
   }
}
