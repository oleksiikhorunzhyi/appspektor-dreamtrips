package com.worldventures.dreamtrips.api.smart_card.user_association.model;


import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface AssociationCardUserData {

    @SerializedName("scID")
    long scid();

    @SerializedName("deviceId")
    String deviceId();

    @SerializedName("pairedDeviceModel")
    String deviceModel();

    @SerializedName("pairedDeviceOsVersion")
    String deviceOsVersion();

    @SerializedName("acceptedTACVersion")
    int acceptedTermsAndConditionVersion();

    @Nullable
    @SerializedName("user")
    AssociationUserData user();
}
