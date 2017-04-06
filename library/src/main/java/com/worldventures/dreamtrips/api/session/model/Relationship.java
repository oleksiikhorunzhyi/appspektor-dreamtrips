package com.worldventures.dreamtrips.api.session.model;

import com.google.gson.annotations.SerializedName;

public enum Relationship {
    @SerializedName("none")
    NONE,
    @SerializedName("friend")
    FRIEND,
    @SerializedName("incoming_request")
    INCOMING_REQUEST,
    @SerializedName("outgoing_request")
    OUTGOING_REQUEST,
    @SerializedName("rejected")
    REJECTED,

    UNKNOWN
}
