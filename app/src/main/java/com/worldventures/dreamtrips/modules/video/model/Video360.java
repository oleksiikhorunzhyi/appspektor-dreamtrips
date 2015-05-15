package com.worldventures.dreamtrips.modules.video.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

public class Video360 {
    @SerializedName("Order")
    private int order;
    @SerializedName("Thumbnail")
    private String thumbnail;
    @SerializedName("Title")
    private String title;
    @SerializedName("URL")
    private String uRL;
    @SerializedName("duration")
    private int duration;

    private CachedEntity cacheEntity;

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getURL() {
        return this.uRL;
    }

    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    public String getDuration() {
        return duration != 0 ? DateTimeUtils.convertSecondsToString(duration) : "";
    }

    public CachedEntity getCacheEntity() {
        if (cacheEntity == null) {
            cacheEntity = new CachedEntity(this.getURL(), this.getUid());
        }
        return cacheEntity;
    }

    public void setCacheEntity(CachedEntity cacheEntity) {
        this.cacheEntity = cacheEntity;
    }

    public String getUid() {
        return getURL();
    }
}
