package com.worldventures.dreamtrips.api.fixtures;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface FirmwareData {
    @SerializedName("firmware_version")
    String firmware();
    @SerializedName("sdk_version")
    String sdk();
}
