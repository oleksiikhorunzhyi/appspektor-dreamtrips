package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import timber.log.Timber;

public final class SCFirmwareUtils {

   private static final int MIN_BATTERY_LEVEL = 10;
   private final static int SUPPORTED_CHARGER_ACTION_VERSION_FW = 1052;
   private final static int SUPPORTED_ON_CARD_ANALYTICS_VERSION_FW = 1070;

   private final static String UNKNOWN_VERSION = "0.0.0.0";//NOPMD

   private SCFirmwareUtils() {}

   public static String smartCardFirmwareVersion(@Nullable SmartCardFirmware firmware) {
      return firmware != null ? firmware.nordicAppVersion() : "";
   }

   public static boolean isNewFirmwareAvailable(String currentVersion, String availableVersion) {
      return currentVersion.isEmpty()
            || (!UNKNOWN_VERSION.equals(currentVersion) && !currentVersion.equalsIgnoreCase(availableVersion));
   }

   public static boolean cardIsCharged(int batteryLevel, boolean cardInCharger) {
      return batteryLevel >= MIN_BATTERY_LEVEL || cardInCharger;
   }

   public static boolean chargerRequired(@Nullable SmartCardFirmware firmware) {
      if (firmware != null) {
         int firmwareVersion = firmwareVersionStringToInt(firmware.nordicAppVersion());
         return firmwareVersion == 0 || firmwareVersion == SUPPORTED_CHARGER_ACTION_VERSION_FW;
      }
      return false;
   }

   public static boolean supportOnCardAnalytics(@Nullable SmartCardFirmware firmware) {
      return firmware != null && firmwareVersionStringToInt(firmware.nordicAppVersion()) >= SUPPORTED_ON_CARD_ANALYTICS_VERSION_FW;
   }

   private static int firmwareVersionStringToInt(@Nullable String firmwareVersion) {
      if (firmwareVersion != null) {
         try {
            String parsed = firmwareVersion.replaceAll("\\.", "").intern();
            return Integer.parseInt(parsed);
         } catch (Exception e) {
            Timber.e(e, "Cannot parse to Integer, Method: firmwareVersionStringToInt, value: %s", firmwareVersion);
         }
      }
      return 0;
   }

   public static String obtainRecordVersion(String nordicVersion) {
      return firmwareVersionStringToInt(nordicVersion) >= 1080 ? "2.0.0" : "1.0.78";
   }

}
