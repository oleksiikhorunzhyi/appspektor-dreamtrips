package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.core.model.User;

public class UserSession {
    private User user;
    private String apiToken;
    private String legacyApiToken;
    private String userPassword;
    private String username;
    private long lastUpdate;

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

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
