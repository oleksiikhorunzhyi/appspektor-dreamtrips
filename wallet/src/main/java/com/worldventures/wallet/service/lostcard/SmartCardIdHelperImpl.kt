package com.worldventures.wallet.service.lostcard

import com.worldventures.core.model.session.Feature
import com.worldventures.core.model.session.UserSession
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand
import rx.Observable

internal class SmartCardIdHelperImpl(
      private val smartCardInteractor: SmartCardInteractor,
      private val locationInteractor: SmartCardLocationInteractor) : SmartCardIdHelper {

   override fun fetchSmartCardFromServer(userSession: UserSession) {
      userSession.permissions().find { it.name == Feature.WALLET } ?: return

      smartCardInteractor.fetchAssociatedSmartCard()
            .createObservableResult(FetchAssociatedSmartCardCommand())
            .doOnNext {
               smartCardInteractor.fetchAssociatedSmartCard().clearReplays()
               locationInteractor.fetchTrackingStatusPipe().send(FetchTrackingStatusCommand()) }
            .subscribe()
   }

   override fun smartCardIdObservable(): Observable<String?> = Observable.merge(
         smartCardInteractor.activeSmartCardPipe().observeSuccessWithReplay().map { it.result },
         smartCardInteractor.fetchAssociatedSmartCard().observeSuccessWithReplay().map { it.result.smartCard }
   )
         .distinctUntilChanged()
         .map { it?.smartCardId }
         .doOnSubscribe { smartCardInteractor.activeSmartCardPipe().send(ActiveSmartCardCommand()) }
         .mergeWith(smartCardInteractor.wipeSmartCardDataPipe().observeSuccess().map { null })
}
