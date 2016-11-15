package com.worldventures.dreamtrips.wallet.service.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;

import rx.Observable;
import rx.Subscriber;

public class AndroidBleService implements WalletBluetoothService {

   private final Context appContext;
   private final BluetoothManager bluetoothManager;

   public AndroidBleService(Context appContext) {
      this.appContext = appContext;
      bluetoothManager = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
   }

   @Override
   public boolean isSupported() {
      return appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
   }

   @Override
   public boolean isEnable() {
      BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
      return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
   }

   @Override
   public Observable<Boolean> observeEnablesState() {
      BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
      if (bluetoothAdapter == null) return Observable.empty();

      RxBluetoothAdapter adapter = new RxBluetoothAdapter(appContext);
      return Observable.create(adapter)
            .doOnUnsubscribe(adapter::release);
   }

   private static class RxBluetoothAdapter implements Observable.OnSubscribe<Boolean> {

      private final Context appContext;
      private BroadcastReceiver broadcastReceiver;

      private RxBluetoothAdapter(Context appContext) {
         this.appContext = appContext;
      }

      @Override
      public void call(Subscriber<? super Boolean> subscriber) {
         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
               subscriber.onNext(BluetoothAdapter.STATE_ON == state);
            }
         };
         appContext.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
      }

      public void release() {
         appContext.unregisterReceiver(broadcastReceiver);
      }
   }
}
