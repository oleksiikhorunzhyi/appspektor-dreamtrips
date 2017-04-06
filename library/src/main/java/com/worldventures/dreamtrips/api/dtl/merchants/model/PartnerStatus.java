package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

public enum PartnerStatus {
    @SerializedName("participant")PARTICIPANT,
    @SerializedName("prospect")PROSPECT,
    @SerializedName("excluded")EXCLUDED,
    @SerializedName("pending")PENDING,
    UNKNOWN
}
