package com.worldventures.dreamtrips.wallet.service.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;

import rx.Observable;

public class AndroidNetworkManager implements WalletNetworkService {

   private final Context appContext;

   public AndroidNetworkManager(Context appContext) {
      this.appContext = appContext;
   }

   @Override
   public boolean isAvailable() {
      final ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
      return info != null && info.isConnected();
   }

   @Override
   public Observable<Boolean> observeConnectedState() {
      return ReactiveNetwork.observeNetworkConnectivity(appContext)
            .map(connectivity -> connectivity.getState() == NetworkInfo.State.CONNECTED);
   }
}
