package com.worldventures.dreamtrips.api.smart_card.bank_info.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Gson.TypeAdapters
@Value.Immutable
public interface BankInfo {

    @SerializedName("bin")
    String bin();

    @SerializedName("brand")
    String brand();

    @SerializedName("sub_brand")
    String subBrand();

    @SerializedName("country_code")
    String countryCode();

    @SerializedName("country_name")
    String countryName();

    @SerializedName("card_type")
    String cardType();

    @SerializedName("card_category")
    String cardCategory();

    @Nullable
    @SerializedName("bank")
    String bank();

    @SerializedName("latitude")
    long latitude();

    @SerializedName("longitude")
    long longitude();
}
