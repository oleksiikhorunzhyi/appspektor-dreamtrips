package com.worldventures.dreamtrips.wallet.service;

import rx.Observable;

public interface WalletNetworkService {

   boolean isAvailable();

   Observable<Boolean> observeConnectedState();
}
