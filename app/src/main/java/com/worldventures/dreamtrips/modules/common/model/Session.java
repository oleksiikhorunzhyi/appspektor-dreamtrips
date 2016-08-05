package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.List;

public class Session {

    private String token;
    @SerializedName("sso_token")
    private String ssoToken;
    private User user;
    private String locale;
    private List<Feature> permissions;
    private List<Setting> settings;

    public String getToken() {
        return token;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public User getUser() {
        return user;
    }

    public String getLocale() {
        return locale;
    }

    public List<Feature> getPermissions() {
        return permissions;
    }

    public List<Setting> getSettings() {
        return settings;
    }
}