package com.worldventures.dreamtrips.wallet.util;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import org.jetbrains.annotations.Nullable;

public final class SCFirmwareUtils {

   private SCFirmwareUtils() {}

   public static String smartCardFirmwareVersion(@Nullable SmartCardFirmware firmware) {
      return firmware != null ? firmware.firmwareVersion() : "";
   }
}
