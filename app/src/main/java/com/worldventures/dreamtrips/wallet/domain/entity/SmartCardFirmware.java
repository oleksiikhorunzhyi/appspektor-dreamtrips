package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

@Value.Immutable
public interface SmartCardFirmware {

   String firmwareVersion();

   String nrfBootloaderVersion();

   String internalAtmelVersion();

   String internalAtmelBootloaderVersion();

   String externalAtmelVersion();

   String externalAtmelBootloaderVersion();

}
