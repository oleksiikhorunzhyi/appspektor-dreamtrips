package com.worldventures.dreamtrips.wallet.service;

import rx.Observable;

public interface WalletBluetoothService {

   boolean isSupported();

   boolean isEnable();

   Observable<Boolean> observeEnablesState();
}
