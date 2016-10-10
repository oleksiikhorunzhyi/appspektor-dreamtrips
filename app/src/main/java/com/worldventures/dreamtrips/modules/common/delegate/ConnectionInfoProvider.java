package com.worldventures.dreamtrips.modules.common.delegate;


import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionInfoProvider {

   private Context context;

   public ConnectionInfoProvider(Context context) {
      this.context = context;
   }

   public boolean isWifi() {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
   }
}
