package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Value.Immutable
public abstract class FirmwareInfo implements Serializable {

   @Value.Default
   public long byteSize() {
      return 0;
   }

   @Value.Default
   public String downloadUrl() {
      return "";
   }

   public abstract boolean isCompatible();

   @Nullable
   public abstract String versionName();

   @Nullable
   public abstract String releaseNotes();
}
