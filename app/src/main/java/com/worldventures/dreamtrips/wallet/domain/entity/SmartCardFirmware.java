package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface SmartCardFirmware {

   @Nullable
   String firmwareBundleVersion();

   String nordicAppVersion();

   String nrfBootloaderVersion();

   String internalAtmelVersion();

   String internalAtmelBootloaderVersion();

   String externalAtmelVersion();

   String externalAtmelBootloaderVersion();

}
