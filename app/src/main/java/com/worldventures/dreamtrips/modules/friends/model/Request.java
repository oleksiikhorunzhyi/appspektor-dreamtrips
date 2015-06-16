package com.worldventures.dreamtrips.modules.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Date;

public class Request {

    public static final String INCOMING = "INCOMING";
    public static final String OUTGOING = "OUTGOING";
    public static final String REJECTED = "REJECTED";
    public static final String PENDING = "PENDING";

    String direction;
    String status;
    @SerializedName("request_date")
    Date requestDate;
    @SerializedName("user")
    User user;

    public String getDirection() {
        return direction;
    }

    public String getStatus() {
        return status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public User getUser() {
        return user;
    }
}
