package com.worldventures.dreamtrips.wallet.util;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public final class SCFirmwareUtils {
   public static final int MIN_BATTERY_LEVEL = 50;
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

   public static int firmwareStringToInt(String firmwareVersion) {
      if (firmwareVersion != null && !firmwareVersion.contains("test")) {
         try {
            String parsed = firmwareVersion.replaceAll("\\.", "").intern();
            return Integer.parseInt(parsed);
         } catch (Exception e) {
            Timber.e(e, "CAN`T parce to Int, Method: firmwareStringToInt, value: %s", firmwareVersion);
            return SUPPORTED_CHARGER_ACTION_VERSION_FW;
         }
      } else
         return SUPPORTED_CHARGER_ACTION_VERSION_FW;
   }

   public static boolean cardIsCharged(int batteryLevel) {
      return batteryLevel >= MIN_BATTERY_LEVEL;
   }

   public static boolean chargerRequered(FirmwareUpdateData data) {
      return firmwareStringToInt(data.currentFirmwareVersion().nordicAppVersion()) == SUPPORTED_CHARGER_ACTION_VERSION_FW;
   }
}
