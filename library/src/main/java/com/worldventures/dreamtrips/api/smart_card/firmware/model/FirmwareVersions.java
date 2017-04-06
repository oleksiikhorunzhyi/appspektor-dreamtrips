package com.worldventures.dreamtrips.api.smart_card.firmware.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FirmwareVersions {

    @SerializedName("app_atmel_version")
    String atmelVersion();

    @SerializedName("app_nordic_version")
    String nordicVersion();

    @SerializedName("bootloader_nordic_version")
    String bootloaderNordicVersion();

    @SerializedName("puck_atmel_version")
    String puckAtmelVerstion();

}
