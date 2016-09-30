package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Value.Immutable
public abstract class FirmwareInfo implements Serializable {

   public abstract boolean isCompatible();

   @Nullable
   public abstract String versionName();

   @Nullable
   public abstract String releaseNotes();

   public abstract String id();

   @Nullable
   public abstract String firmwareName();

   @Nullable
   public abstract String firmwareVersion();

   @Nullable
   public abstract String sdkVersion();

   public int fileSize() { // bytes
      return 0;
   }

   public abstract String url();
}
