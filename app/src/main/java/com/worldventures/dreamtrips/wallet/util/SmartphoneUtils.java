package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import timber.log.Timber;

public class SmartphoneUtils {

   private static String deviceId;

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

   /**
    * @param context for obtaining services
    * @return unique device identifier, which is composed by the formula:
    * android_id + serial number + pseudo_device_id (it's composed from system properties of device)
    * If there are bugs in retrieving all components, an Exception will be thrown
    */
   public static synchronized String getDeviceIdentifier(Context context) {
      if (deviceId != null) return deviceId;

      StringBuilder sb = new StringBuilder();
      String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      String pseudoDeviceId = getPseudoDeviceId();
      String serialNumber = Build.SERIAL;

      if (androidId != null) sb.append(androidId);
      if (pseudoDeviceId != null) sb.append(pseudoDeviceId);
      if (serialNumber != null && !TextUtils.equals(serialNumber, "unknown")) sb.append(serialNumber);

      if (sb.length() == 0)
         throw new RuntimeException("Unique device indentifier wasn't obtained");

      return deviceId = sb.toString();
   }

   @Nullable
   private static String getPseudoDeviceId() {
      String result = null;
      try {
         result = "35" + //we make this look like a valid IMEI
               Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
               Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
               Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
               Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
               Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
               Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
               Build.USER.length() % 10; //13 digits
      } catch (Exception exception) { // in case china devices
         Timber.e(exception, "Error while fetching PseudoDeviceId");
      }

      return result;
   }

}
