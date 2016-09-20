package com.worldventures.dreamtrips.wallet.domain.entity;


import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface FirmwareInfo {

   @Nullable
   Long byteSize();

   long versionCode();

   @Nullable
   String versionName();
   
   @Nullable
   String releaseNotes();
}
