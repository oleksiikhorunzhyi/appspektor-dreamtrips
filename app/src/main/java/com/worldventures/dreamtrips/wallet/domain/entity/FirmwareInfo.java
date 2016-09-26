package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public abstract class FirmwareInfo {

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
