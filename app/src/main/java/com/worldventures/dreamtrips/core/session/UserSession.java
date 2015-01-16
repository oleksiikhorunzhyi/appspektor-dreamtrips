package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.core.model.User;

public class UserSession {
    private User user;
    private String apiToken;
    private String legacyApiToken;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getLegacyApiToken() {
        return legacyApiToken;
    }

    public void setLegacyApiToken(String legacyApiToken) {
        this.legacyApiToken = legacyApiToken;
    }
}
