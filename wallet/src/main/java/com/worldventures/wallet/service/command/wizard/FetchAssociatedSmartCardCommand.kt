package com.worldventures.wallet.service.command.wizard

import com.worldventures.dreamtrips.api.smart_card.user_association.GetAssociatedCardsHttpAction
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.SystemPropertiesProvider
import com.worldventures.wallet.service.command.ConnectSmartCardCommand
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

@Suppress("UnsafeCallOnNullableType")
@CommandAction
class FetchAssociatedSmartCardCommand(private val skipLocalData: Boolean = false)
   : Command<FetchAssociatedSmartCardCommand.AssociatedCard>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)] lateinit var janetWallet: Janet
   @Inject lateinit var janet: Janet
   @Inject lateinit var propertiesProvider: SystemPropertiesProvider
   @Inject lateinit var walletStorage: WalletStorage
   @Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<FetchAssociatedSmartCardCommand.AssociatedCard>) {
      if (!skipLocalData && findInLocalCash(callback)) {
         return
      }
      janet.createPipe(GetAssociatedCardsHttpAction::class.java)
            .createObservableResult(GetAssociatedCardsHttpAction(propertiesProvider.deviceId()))
            .flatMap { action -> handleResponse(action.response()) }
            .doOnNext {
               if (it.exist) {
                  janetWallet.createPipe(ConnectSmartCardCommand::class.java)
                        .send(ConnectSmartCardCommand(it.smartCard!!.smartCardId))
               }
            }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun handleResponse(listSmartCardInfo: List<SmartCardInfo>): Observable<FetchAssociatedSmartCardCommand.AssociatedCard> {
      if (listSmartCardInfo.isEmpty()) {
         return Observable.just(AssociatedCard())
      }
      val smartCardInfo = listSmartCardInfo.first()

      return ProcessSmartCardInfoDelegate(janetWallet, mappery)
            .processSmartCardInfo(smartCardInfo)
            .map { (smartCard, _) -> AssociatedCard(smartCard = smartCard) }
   }

   private fun findInLocalCash(callback: CommandCallback<FetchAssociatedSmartCardCommand.AssociatedCard>): Boolean {
      val smartCard = walletStorage.smartCard
      val user = walletStorage.smartCardUser
      if (smartCard != null && smartCard.cardStatus === CardStatus.ACTIVE && user != null) {
         callback.onSuccess(AssociatedCard(smartCard = smartCard))
         return true
      }
      return false
   }

   class AssociatedCard(val smartCard: SmartCard? = null) {
      val exist: Boolean
         get() = smartCard != null
   }
}
