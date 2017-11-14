package com.worldventures.wallet.domain.entity;

import android.support.annotation.NonNull;

import org.immutables.value.Value;

@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class AboutSmartCardData {

   public static AboutSmartCardData of(@NonNull SmartCardFirmware firmware) {
      return ImmutableAboutSmartCardData.of(firmware);
   }

   @Value.Parameter
   public abstract SmartCardFirmware smartCardFirmware();
}
