package com.worldventures.dreamtrips.api.smart_card.association_info.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface SmartCardInfo {

    @SerializedName("scID")
    long scId();

    @SerializedName("acceptedTACVersion")
    int acceptedTACVersion();

    @SerializedName("serialNumber")
    String serialNumber();

    @SerializedName("bleAddress")
    String bleAddress();

    @SerializedName("revVersion")
    String revVersion();

    @SerializedName("wvOrderId")
    String wvOrderId();

    @SerializedName("nxtOrderId")
    String nxtOrderId();

    @SerializedName("orderDate")
    Date orderDate();

    @SerializedName("deviceId")
    @Nullable
    String deviceId();

    @SerializedName("user")
    SmartCardUser user();
}
