package com.worldventures.dreamtrips.api.friends.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Date;

@Gson.TypeAdapters
@Value.Immutable
public interface FriendRequestResponse {
    @SerializedName("user_id")
    int userId();

    @SerializedName("status")
    Status status();

    @SerializedName("request_date")
    Date requestDate();

    @SerializedName("confirmation_date")
    Date responseDate();

    enum Status {
        @SerializedName("Confirm")
        CONFIRM,
        @SerializedName("Reject")
        REJECT,
        UNKNOWN
    }
}
