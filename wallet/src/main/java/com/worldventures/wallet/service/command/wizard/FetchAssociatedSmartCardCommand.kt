package com.worldventures.wallet.service.command.wizard

import com.worldventures.dreamtrips.api.smart_card.user_association.GetAssociatedCardsHttpAction
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardDetails
import com.worldventures.wallet.domain.entity.SmartCardUser
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
class FetchAssociatedSmartCardCommand : Command<FetchAssociatedSmartCardCommand.AssociatedCard>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)]
   lateinit var janetWallet: Janet
   @Inject lateinit var janet: Janet
   @Inject lateinit var propertiesProvider: SystemPropertiesProvider
   @Inject lateinit var walletStorage: WalletStorage
   @Inject lateinit var mappery: MapperyContext

   private val smartCardFromCache: SmartCard?
      get() = walletStorage.smartCard

   private val smartCardUserFromCache: SmartCardUser?
      get() = walletStorage.smartCardUser

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<FetchAssociatedSmartCardCommand.AssociatedCard>) {
      val smartCard = smartCardFromCache
      val user = smartCardUserFromCache
      if (smartCard != null && smartCard.cardStatus === CardStatus.ACTIVE && user != null
            // TODO: this check for backward compatibility 1.21 -> 1.23
            && user.userPhoto != null && user.userPhoto.uri != null) {
         callback.onSuccess(createAssociatedCard(smartCard, walletStorage.smartCardDetails))
         return
      }
      janet.createPipe(GetAssociatedCardsHttpAction::class.java)
            .createObservableResult(GetAssociatedCardsHttpAction(propertiesProvider.deviceId()))
            .flatMap { action -> handleResponse(action.response()) }
            .doOnNext { result ->
               if (result.exist) {
                  janetWallet.createPipe(ConnectSmartCardCommand::class.java)
                        .send(ConnectSmartCardCommand(result.smartCard!!.smartCardId))
               }
            }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun handleResponse(listSmartCardInfo: List<SmartCardInfo>): Observable<FetchAssociatedSmartCardCommand.AssociatedCard> {
      if (listSmartCardInfo.isEmpty()) {
         return Observable.just(AssociatedCard(exist = false))
      }
      val smartCardInfo = listSmartCardInfo[0]

      return ProcessSmartCardInfoDelegate(walletStorage, janetWallet, mappery)
            .processSmartCardInfo(smartCardInfo)
            .map { result -> createAssociatedCard(result.smartCard, result.details) }
   }

   private fun createAssociatedCard(smartCard: SmartCard, smartCardDetails: SmartCardDetails): AssociatedCard {
      return AssociatedCard(
            exist = true,
            smartCard = smartCard,
            smartCardDetails = smartCardDetails)
   }

   data class AssociatedCard(
         val smartCard: SmartCard? = null,
         val smartCardDetails: SmartCardDetails? = null,
         val exist: Boolean
   )
}
