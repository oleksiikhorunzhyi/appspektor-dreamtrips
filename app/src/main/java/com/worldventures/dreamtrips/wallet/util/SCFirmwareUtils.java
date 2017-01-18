package com.worldventures.dreamtrips.wallet.util;

import android.util.Log;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;

import org.jetbrains.annotations.Nullable;

public final class SCFirmwareUtils {

   private SCFirmwareUtils() {}

   public static String smartCardFirmwareVersion(@Nullable SmartCardFirmware firmware) {
      return firmware != null ? firmware.nordicAppVersion() : "";
   }

   public static boolean newFirmwareAvailable(String currentVersion, String availableVersion) {
      return !currentVersion.equalsIgnoreCase(availableVersion);
   }

   public static int firmwareStringToInt(String firmwareVersion) {
      if (firmwareVersion != null && !firmwareVersion.contains("test")) {
         try {
            String parsed = firmwareVersion.replaceAll("\\.", "").intern();
            return Integer.parseInt(parsed);
         } catch (Exception e) {
            Log.e("SCFirmwareUtils", "CAN`T parce to Int, Method: firmwareStringToInt, value: " + firmwareVersion);
            return PreInstallationCheckCommand.SUPPORTED_CHARGER_ACTION_VERSION_FW;
         }
      } else
         return PreInstallationCheckCommand.SUPPORTED_CHARGER_ACTION_VERSION_FW;
   }
}
