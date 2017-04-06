package com.worldventures.dreamtrips.api.smart_card.firmware.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface FirmwareInfo {

    @SerializedName("id")
    String id();

    @SerializedName("firmware_name")
    String firmwareName();

    @SerializedName("firmware_version")
    String firmwareVersion();

    @SerializedName("sdk_version")
    String sdkVersion();

    @SerializedName("file_size")
    int fileSize(); // bytes

    @SerializedName("firmware")
    FirmwareVersions firmwareVersions();

    @SerializedName("release_notes")
    String releaseNotes();

    @SerializedName("is_compatible")
    boolean isCompatible();

    @SerializedName("url")
    String url();
}
