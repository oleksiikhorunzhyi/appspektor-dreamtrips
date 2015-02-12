package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class S3GlobalConfig {
    @SerializedName("FlagContent")
    FlagList flagContent;
    @SerializedName("URLS")
    URLS urls;
    @SerializedName("Videos360")
    List<Videos360> videos360;
    @SerializedName("server_status")
    ServerStatus serverStatus;
    @SerializedName("facebook_gallery_enabled")
    boolean facebook_gallery_enabled;

    public FlagList getFlagContent() {
        return this.flagContent;
    }

    public void setFlagContent(FlagList flagContent) {
        this.flagContent = flagContent;
    }

    public URLS getUrls() {
        return this.urls;
    }

    public void setUrls(URLS uRLS) {
        this.urls = uRLS;
    }

    public List<Videos360> getVideos360() {
        return this.videos360;
    }

    public void setVideos360(List<Videos360> videos360) {
        this.videos360 = videos360;
    }

    public ServerStatus getServerStatus() {
        return this.serverStatus;
    }

    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public boolean isFacebook_gallery_enabled() {
        return facebook_gallery_enabled;
    }

    public void setFacebook_gallery_enabled(boolean facebook_gallery_enabled) {
        this.facebook_gallery_enabled = facebook_gallery_enabled;
    }
}
