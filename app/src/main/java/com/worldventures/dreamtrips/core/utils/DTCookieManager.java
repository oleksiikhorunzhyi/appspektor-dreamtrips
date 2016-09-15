package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class DTCookieManager {

   private Context context;

   public DTCookieManager(Context context) {
      this.context = context;
   }

   public void clearCookies() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
         CookieManager.getInstance().removeAllCookies(null);
         CookieManager.getInstance().flush();
      } else {
         CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
         cookieSyncMngr.startSync();
         CookieManager cookieManager = CookieManager.getInstance();
         cookieManager.removeAllCookie();
         cookieManager.removeSessionCookie();
         cookieSyncMngr.stopSync();
         cookieSyncMngr.sync();
      }
   }
}
