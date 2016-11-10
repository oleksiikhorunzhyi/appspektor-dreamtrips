package com.messenger.util;

import android.support.v4.util.ArrayMap;

import com.crashlytics.android.Crashlytics;

import java.util.Set;

public final class CrashlyticsTracker {

   public static void trackError(Throwable throwable) {
      Crashlytics.logException(throwable);
   }

   public static void trackErrorWithParams(Throwable throwable, ArrayMap<String, ?> paramsMap) {
      Set<String> keys = paramsMap.keySet();
      for (String key : keys) {
         Crashlytics.log(key + " : " + paramsMap.get(key));
      }

      Crashlytics.logException(throwable);
   }
}
