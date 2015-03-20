package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Header;

public class UserSession {
    private User user;
    private String apiToken;
    private String legacyApiToken;
    private String userPassword;
    private String username;
    private long lastUpdate;
    private S3GlobalConfig globalConfig;
    private List<Header> headerList;

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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public S3GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(S3GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public List<Header> getHeaderList() {
        return headerList != null ? headerList : new ArrayList<>();
    }

    public void setHeaderList(List<Header> headerList) {
        this.headerList = headerList;
    }
}
