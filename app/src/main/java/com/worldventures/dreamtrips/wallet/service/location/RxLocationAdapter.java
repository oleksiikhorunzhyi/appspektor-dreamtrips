package com.worldventures.dreamtrips.wallet.service.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;

import rx.Observable;
import rx.Subscriber;

class RxLocationAdapter implements Observable.OnSubscribe<Void> {
   private final Context appContext;
   private BroadcastReceiver broadcastReceiver;

   RxLocationAdapter(Context appContext) {
      this.appContext = appContext;
   }

   @Override
   public void call(Subscriber<? super Void> subscriber) {
      broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            subscriber.onNext(null);
         }
      };
      appContext.registerReceiver(broadcastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
   }

   public void release() {
      appContext.unregisterReceiver(broadcastReceiver);
   }
}
