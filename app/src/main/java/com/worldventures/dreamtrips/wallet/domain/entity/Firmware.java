package com.worldventures.dreamtrips.wallet.domain.entity;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Value.Immutable
public abstract class Firmware implements Serializable {

   @Value.Default
   public boolean updateAvailable() {
      return false;
   }

   @Nullable
   public abstract FirmwareInfo firmwareInfo();
}