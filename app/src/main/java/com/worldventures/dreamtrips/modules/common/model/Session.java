package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.session.acl.Feature;

import java.util.List;

public class Session extends BaseEntity {

    private String token;
    @SerializedName("sso_token")
    private String ssoToken;
    private User user;
    private List<Feature> permissions;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Feature> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Feature> permissions) {
        this.permissions = permissions;
    }
}