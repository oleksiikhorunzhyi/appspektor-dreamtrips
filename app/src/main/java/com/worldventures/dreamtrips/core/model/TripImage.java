package com.worldventures.dreamtrips.core.model;

public class TripImage extends BaseEntity {

    String description;
    String url;
    String type;
    String original_url;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginalUrl() {
        return original_url;
    }

    public void setOriginalUrl(String originalUrl) {
        this.original_url = originalUrl;
    }
}
