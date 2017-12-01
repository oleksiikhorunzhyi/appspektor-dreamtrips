package com.worldventures.wallet.service.lostcard

import com.worldventures.core.model.session.UserSession
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand
import rx.Observable
import rx.lang.kotlin.PublishSubject

class MockSmartCardIdHelper(private val locationInteractor: SmartCardLocationInteractor) : SmartCardIdHelper {

   private val smartCardIdSubject = PublishSubject<String?>()
   var initValue: String? = null

   override fun smartCardIdObservable(): Observable<String?> = smartCardIdSubject.asObservable()
         .startWith(Observable.fromCallable { initValue }.filter({ it != null }))

   override fun fetchSmartCardFromServer(userSession: UserSession) {
      locationInteractor.fetchTrackingStatusPipe().send(FetchTrackingStatusCommand())
   }

   fun pushNewSmartCardId(smartCardId: String?) {
      smartCardIdSubject.onNext(smartCardId)
   }
}