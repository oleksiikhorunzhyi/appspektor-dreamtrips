package com.worldventures.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class SdkVersionUtils {
   private SdkVersionUtils() {
   }

   private static int targetSdkVersion = -1;

   public static int getTargetSdkVersion(Context context) {
      if (targetSdkVersion != -1) return targetSdkVersion;
      try {
         PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
         targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion;
      } catch (PackageManager.NameNotFoundException ignored) {
      }
      return targetSdkVersion;
   }
}
