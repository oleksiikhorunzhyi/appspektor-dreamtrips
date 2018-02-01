package com.worldventures.wallet.service.impl

import com.worldventures.wallet.service.WalletNetworkService
import rx.Observable
import rx.subjects.PublishSubject

class MockWalletNetworkService(var available: Boolean = true) : WalletNetworkService {

   private val connectedStateSubject = PublishSubject.create<Boolean>()

   override fun isAvailable() = available

   override fun observeConnectedState(): Observable<Boolean> = connectedStateSubject.asObservable()

   fun pushNewConnectedState(available: Boolean) {
      connectedStateSubject.onNext(available)
   }
}
