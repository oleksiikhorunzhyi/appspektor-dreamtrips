package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface ProfileAddress {

    @SerializedName("type")
    AddressType type();

    @SerializedName("address_line1")
    String address1();

    @Nullable
    @SerializedName("address_line2")
    String address2();

    @SerializedName("city")
    String city();

    @SerializedName("country")
    String country();

    @SerializedName("state")
    String state();

    @SerializedName("zip_code")
    String zipCode();
}
