package com.worldventures.dreamtrips.wallet.util;


import android.os.Build;
import android.text.TextUtils;

public class SmartphoneUtils {

   private SmartphoneUtils() {
   }

   public static String getDeviceName() {
      String manufacturer = Build.MANUFACTURER;
      String model = Build.MODEL;

      return model.startsWith(manufacturer) ? capitalize(model) : String.format("%s %s", capitalize(manufacturer), model);
   }

   private static String capitalize(String name) {
      if (TextUtils.isEmpty(name)) return "";

      char firstLetter = name.charAt(0);
      return Character.isUpperCase(firstLetter) ? name : Character.toUpperCase(firstLetter) + name.substring(1);
   }

   public static String getOsVersion() {
      return "Android " + Build.VERSION.RELEASE;
   }

}
