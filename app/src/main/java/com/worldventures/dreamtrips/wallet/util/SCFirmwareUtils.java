package com.worldventures.dreamtrips.wallet.util;

import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import org.jetbrains.annotations.Nullable;

public final class SCFirmwareUtils {

   private SCFirmwareUtils() {}

   public static String smartCardFirmwareVersion(@Nullable SmartCardFirmware firmware) {
      return firmware != null ? firmware.firmwareVersion() : "";
   }

   public static boolean newFirmwareAvailable(String currentVersion, String availableVersion) {
      //todo remove from mock
      currentVersion = currentVersion.endsWith("-mock")? currentVersion.substring(0, currentVersion.length() -5) : currentVersion;

      if (TextUtils.isEmpty(availableVersion)) return false;
      else if (TextUtils.isEmpty(currentVersion)) return true;

      String currentSubversions[] = currentVersion.split("\\.");
      String availableSubversions[] = availableVersion.split("\\.");
      int minSubversionsLenght = Math.min(currentSubversions.length, availableSubversions.length);

      for(int i = 0; i < minSubversionsLenght; i++) {
         if (Integer.parseInt(availableSubversions[i]) > Integer.parseInt(currentSubversions[i])) return true;
      }
      return false;
   }

}
