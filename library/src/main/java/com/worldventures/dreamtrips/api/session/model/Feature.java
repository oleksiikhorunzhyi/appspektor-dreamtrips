package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Feature {

    @SerializedName("name")
    FeatureName name();

    enum FeatureName {
        @SerializedName("trips")TRIPS,
        @SerializedName("repTools")REP_TOOLS,
        @SerializedName("social")SOCIAL,
        @SerializedName("discover")DTL,
        @SerializedName("repSuggestMerchant")REP_SUGGEST_MERCHANT,
        @SerializedName("bookTravel")BOOK_TRAVEL,
        @SerializedName("bookTrip")BOOK_TRIP,
        @SerializedName("membership")MEMBERSHIP,
        @SerializedName("wallet") WALLET,
        @SerializedName("wallet_provisioning") WALLET_PROVISIONING,

        UNKNOWN
    }


}
