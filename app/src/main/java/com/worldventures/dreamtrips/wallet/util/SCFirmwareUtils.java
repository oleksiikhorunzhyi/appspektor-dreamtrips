package com.worldventures.dreamtrips.wallet.util;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

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
      String parsed = firmwareVersion.replaceAll("\\.", "").intern();
      return Integer.parseInt(parsed);
   }
}
