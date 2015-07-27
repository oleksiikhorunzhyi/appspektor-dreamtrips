package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TripImage implements Serializable {

    public static final long serialVersionUID = 128L;

    private String id;
    private String description;
    private String url;
    private String type;
    @SerializedName("origin_url")
    private String originUrl;

    public String getUrl() {
        return url != null ? url : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(int width, int height) {
        return getUrl() + String.format(Image.PATTERN, width, height);
    }

    public String getType() {
        return type;
    }

}
