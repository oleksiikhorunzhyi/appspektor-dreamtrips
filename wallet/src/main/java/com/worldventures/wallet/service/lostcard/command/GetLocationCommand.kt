package com.worldventures.wallet.service.lostcard.command

import com.worldventures.dreamtrips.api.smart_card.location.GetSmartCardLocationsHttpAction
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.lostcard.LostCardRepository
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import javax.inject.Inject

@CommandAction
class GetLocationCommand : Command<List<WalletLocation>>(), InjectableAction {

   @Inject lateinit var janet: Janet //todo use WALLET JANET
   @Inject lateinit var locationRepository: LostCardRepository
   @Inject lateinit var smartCardInteractor: SmartCardInteractor
   @Inject lateinit var mapperyContext: MapperyContext

   fun historicalLocationObservable(): Observable<List<WalletLocation>> {
      return observeActiveSmartCard()
            .flatMap { command -> observeGetSmartCardLocations(command.result.smartCardId) }
            .map { it.response() }
            .map { locations -> mapperyContext.convert(locations, WalletLocation::class.java) }
            .onErrorReturn { emptyList() }
   }


   fun storedLocationObservable(): Observable<List<WalletLocation>> {
      val locations = locationRepository.walletLocations
      return if (locations.isEmpty()) Observable.empty() else Observable.just(locations)
   }

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<WalletLocation>>) {
      Observable.concat(storedLocationObservable(), historicalLocationObservable())
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun observeActiveSmartCard() = smartCardInteractor.activeSmartCardPipe()
         .createObservableResult(ActiveSmartCardCommand())

   private fun observeGetSmartCardLocations(smartCardId: String) = janet.createPipe(GetSmartCardLocationsHttpAction::class.java)
         .createObservableResult(GetSmartCardLocationsHttpAction(java.lang.Long.parseLong(smartCardId), PAGE_SIZE))

   companion object {
      private val PAGE_SIZE = 1
   }
}
