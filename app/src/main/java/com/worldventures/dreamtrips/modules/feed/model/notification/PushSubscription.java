package com.worldventures.dreamtrips.modules.feed.model.notification;

import com.google.gson.annotations.SerializedName;

public class PushSubscription {

    private String platform;
    private String token;
    @SerializedName("app_version")
    private String appVersion;
    @SerializedName("os_version")
    private String osVersion;

    public PushSubscription(String platform, String token, String appVersion, String osVersion) {
        this.platform = platform;
        this.token = token;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
    }
}
