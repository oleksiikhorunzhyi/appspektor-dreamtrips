package com.worldventures.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtils {

   private NetworkUtils() {
   }

   public static boolean isConnected(Context context) {
      ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo i = conMgr.getActiveNetworkInfo();
      return i != null && i.isConnected() && i.isAvailable();
   }
}
