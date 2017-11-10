package com.worldventures.dreamtrips.wallet.service.impl

import com.worldventures.dreamtrips.wallet.service.WalletNetworkService
import rx.Observable

class MockWalletNetworkService(private val available: Boolean = true): WalletNetworkService {

   override fun isAvailable() = available

   override fun observeConnectedState(): Observable<Boolean> = Observable.just(available)
}