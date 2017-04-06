package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;

public enum InvitationType {
    @SerializedName("email")
    EMAIL,
    @SerializedName("sms")
    SMS,
    UNKNOWN
}
