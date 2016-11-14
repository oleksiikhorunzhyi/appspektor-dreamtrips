package com.worldventures.dreamtrips.modules.common.delegate.system;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionInfoProvider {

   private Context context;

   public ConnectionInfoProvider(Context context) {
      this.context = context;
   }

   public boolean isWifi() {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
   }
}
