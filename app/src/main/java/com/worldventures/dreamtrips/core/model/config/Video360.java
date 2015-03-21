package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.DateUtils;

public class Video360 {
    @SerializedName("Order")
    private Number order;
    @SerializedName("Thumbnail")
    private String thumbnail;
    @SerializedName("Title")
    private String title;
    @SerializedName("URL")
    private String uRL;
    @SerializedName("duration")
    private int duration;

    public Number getOrder() {
        return this.order;
    }

    public void setOrder(Number order) {
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
        return duration != 0 ? DateUtils.convertSecondsToString(duration) : "";
    }
}
