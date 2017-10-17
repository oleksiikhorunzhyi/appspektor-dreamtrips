package com.worldventures.core.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.worldventures.core.utils.NetworkUtils;


public class ConnectionInfoProviderImpl implements ConnectionInfoProvider {

   private Context context;

   public ConnectionInfoProviderImpl(Context context) {
      this.context = context;
   }

   @Override
   public boolean isWifi() {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
   }

   @Override
   public boolean isConnected() {
      return NetworkUtils.isConnected(context);
   }
}
