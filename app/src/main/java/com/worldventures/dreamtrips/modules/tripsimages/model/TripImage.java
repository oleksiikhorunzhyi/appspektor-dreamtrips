package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;

import java.io.Serializable;

public class TripImage implements Serializable {
    public static final long serialVersionUID = 128L;

    private String id;
    private String description;
    private String url;
    private String type;
    @SerializedName("origin_url")
    private String originUrl;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(int width, int height) {
        return url + String.format(UniversalImageLoader.PATTERN, width, height);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginalUrl() {
        return originUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originUrl = originalUrl;
    }

    public String getId() {
        return id;
    }

}
