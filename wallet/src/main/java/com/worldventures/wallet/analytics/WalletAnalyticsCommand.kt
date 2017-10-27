package com.worldventures.wallet.analytics

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
open class WalletAnalyticsCommand(private val walletAnalyticsAction: WalletAnalyticsAction) : Command<Void>(), InjectableAction {

   @Inject internal lateinit var smartCardInteractor: SmartCardInteractor
   @Inject internal lateinit var walletStorage: WalletStorage
   @Inject internal lateinit var analyticsInteractor: WalletAnalyticsInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .subscribe({
               walletAnalyticsAction.setSmartCardAction(walletStorage.smartCard, it.result, walletStorage.smartCardFirmware)
               analyticsInteractor.analyticsActionPipe().send(walletAnalyticsAction)
               callback.onSuccess(null)
            }, callback::onFail)
   }
}
