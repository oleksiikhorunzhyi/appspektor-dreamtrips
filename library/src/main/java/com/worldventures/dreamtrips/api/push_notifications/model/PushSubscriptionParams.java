package com.worldventures.dreamtrips.api.push_notifications.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PushSubscriptionParams {

    @SerializedName("platform")
    @Value.Default
    public Platform platform() {
        return Platform.ANDROID;
    }

    @SerializedName("token")
    public abstract String token();

    @SerializedName("app_version")
    public abstract String appVersion();

    @SerializedName("os_version")
    public abstract String osVersion();

    public enum Platform {
        @SerializedName("android")
        ANDROID,
        @SerializedName("ios")
        IOS
    }
}
