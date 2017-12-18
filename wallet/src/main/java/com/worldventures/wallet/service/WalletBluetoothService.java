package com.worldventures.wallet.service;

import rx.Observable;

public interface WalletBluetoothService {

   boolean isSupported();

   boolean isEnable();

   Observable<Boolean> observeEnablesState();
}
