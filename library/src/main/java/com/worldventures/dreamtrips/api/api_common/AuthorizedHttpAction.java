package com.worldventures.dreamtrips.api.api_common;

import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.ResponseHeader;

public abstract class AuthorizedHttpAction extends BaseHttpAction {

    @RequestHeader("Authorization")
    String authorizationHeader;
    //TODO we should extract from AuthorizedHttpAction action which have response headers only, and update hierarchy
    @ResponseHeader("Friend-Requests-Count")
    String friendRequestCount;
    @ResponseHeader("Unread-Notifications-Count")
    String unreadNotifactionsCount;

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public int getFriendRequestCount() throws IllegalArgumentException {
        return getIntegerValue(friendRequestCount);
    }

    public int getUnreadNotifactionsCount() throws IllegalArgumentException {
        return getIntegerValue(unreadNotifactionsCount);
    }

    private int getIntegerValue(String stringValue) throws IllegalArgumentException {
        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed header value, should be int", e);
        }
    }
}
