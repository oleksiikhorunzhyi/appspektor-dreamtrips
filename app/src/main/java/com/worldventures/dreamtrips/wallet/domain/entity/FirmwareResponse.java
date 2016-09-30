package com.worldventures.dreamtrips.wallet.domain.entity;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface FirmwareResponse {

   @SerializedName("update_required")
   boolean updateAvailable();

   @Nullable
   @SerializedName("update_info")
   FirmwareInfo firmwareInfo();
}