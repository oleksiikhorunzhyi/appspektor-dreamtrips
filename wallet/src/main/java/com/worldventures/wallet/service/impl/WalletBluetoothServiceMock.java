package com.worldventures.wallet.service.impl;

import com.worldventures.wallet.service.WalletBluetoothService;

import rx.Observable;

public class WalletBluetoothServiceMock implements WalletBluetoothService {

   @Override
   public boolean isSupported() {
      return true;
   }

   @Override
   public boolean isEnable() {
      return true;
   }

   @Override
   public Observable<Boolean> observeEnablesState() {
      return Observable.just(true);
   }
}
