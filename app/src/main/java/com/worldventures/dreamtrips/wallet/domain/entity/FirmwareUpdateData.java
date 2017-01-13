package com.worldventures.dreamtrips.wallet.domain.entity;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;

@Value.Immutable
public abstract class FirmwareUpdateData implements Serializable {

   public abstract boolean updateAvailable();

   public abstract boolean updateCritical();

   public abstract boolean factoryResetRequired();

   public abstract String smartCardId();

   public abstract SmartCardFirmware currentFirmwareVersion();

   @Nullable
   public abstract FirmwareInfo firmwareInfo();

   @Nullable
   public abstract File firmwareFile();

   @Value.Derived
   public boolean fileDownloaded() {
      return firmwareFile() != null;
   }
}