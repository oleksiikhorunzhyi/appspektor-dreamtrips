package com.worldventures.dreamtrips.api.smart_card.firmware.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface FirmwareResponse {

    @SerializedName("update_available")
    boolean updateAvailable();

    @SerializedName("update_critical")
    boolean updateCritical();

    @SerializedName("factory_reset_required")
    boolean factoryResetRequired();

    @Nullable
    @SerializedName("update_info")
    FirmwareInfo firmwareInfo();
}
