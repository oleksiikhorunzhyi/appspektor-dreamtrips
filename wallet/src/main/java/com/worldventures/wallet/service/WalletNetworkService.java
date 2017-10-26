package com.worldventures.wallet.service;

import rx.Observable;

public interface WalletNetworkService {

   boolean isAvailable();

   Observable<Boolean> observeConnectedState();
}
