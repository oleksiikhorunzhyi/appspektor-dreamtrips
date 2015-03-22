package com.worldventures.dreamtrips.modules.infopages.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos360 {

    @SerializedName("Order")
    private Number order;

    @SerializedName("Title")
    private String title;

    @SerializedName("Videos")
    private List<Video360> videos;

    public Number getOrder() {
        return this.order;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Video360> getVideos() {
        return this.videos;
    }
}
