package com.worldventures.dreamtrips.wallet.util;

import android.util.Log;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import org.jetbrains.annotations.Nullable;

public final class SCFirmwareUtils {

   public final static int SUPPORTED_CHARGER_ACTION_VERSION_FW = 1052;

   private final static String UNKNOW_VERSION = "0.0.0.0";

   private SCFirmwareUtils() {}

   public static String smartCardFirmwareVersion(@Nullable SmartCardFirmware firmware) {
      return firmware != null ? firmware.nordicAppVersion() : "";
   }

   public static boolean isNewFirmwareAvailable(String currentVersion, String availableVersion) {
      return currentVersion.isEmpty() ||
            (!UNKNOW_VERSION.equals(currentVersion) && !currentVersion.equalsIgnoreCase(availableVersion));
   }

   @Deprecated
   public static boolean isNewFirmwareAvailableForCharger(String currentVersion, String availableVersion) {
      return isNewFirmwareAvailable(currentVersion, availableVersion);
   }

   public static int firmwareStringToInt(String firmwareVersion) {
      if (firmwareVersion != null && !firmwareVersion.contains("test")) {
         try {
            String parsed = firmwareVersion.replaceAll("\\.", "").intern();
            return Integer.parseInt(parsed);
         } catch (Exception e) {
            Log.e("SCFirmwareUtils", "CAN`T parce to Int, Method: firmwareStringToInt, value: " + firmwareVersion);
            return SUPPORTED_CHARGER_ACTION_VERSION_FW;
         }
      } else
         return SUPPORTED_CHARGER_ACTION_VERSION_FW;
   }
}
