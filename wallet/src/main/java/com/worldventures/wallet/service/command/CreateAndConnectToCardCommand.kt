package com.worldventures.wallet.service.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SystemPropertiesProvider
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class CreateAndConnectToCardCommand(private val barcode: String) : Command<SmartCard>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)]
   lateinit var janet: Janet
   @Inject lateinit var propertiesProvider: SystemPropertiesProvider
   @Inject lateinit var interactor: SmartCardInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCard>) {
      val smartCardId = java.lang.Long.valueOf(barcode).toString() //remove zeros from start

      janet.createPipe(ConnectSmartCardCommand::class.java)
            .createObservableResult(ConnectSmartCardCommand(smartCardId))
            .flatMap {
               interactor.activeSmartCardPipe()
                     .createObservableResult(ActiveSmartCardCommand(createSmartCard(smartCardId)))
            }
            .subscribe({ callback.onSuccess(it.result) }, { callback.onFail(it) })
   }

   private fun createSmartCard(scId: String): SmartCard {
      return SmartCard(
            smartCardId = scId,
            cardStatus = CardStatus.IN_PROVISIONING,
            deviceId = propertiesProvider.deviceId())
   }
}
