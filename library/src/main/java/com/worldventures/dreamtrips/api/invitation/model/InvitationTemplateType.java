package com.worldventures.dreamtrips.api.invitation.model;

import com.google.gson.annotations.SerializedName;

public enum InvitationTemplateType {
    @SerializedName("InvitationMemberTemplate")
    MEMBER,
    @SerializedName("InvitationRepTemplate")
    REP,

    UNKNOWN
}
