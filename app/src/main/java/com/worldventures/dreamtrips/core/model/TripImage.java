package com.worldventures.dreamtrips.core.model;

public class TripImage extends BaseEntity {

    private static final String PATTERN = "?width=%d&height=%d";
    String description;
    String url;
    String type;
    String origin_url;

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
        return url + String.format(PATTERN, width, height);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginalUrl() {
        return origin_url;
    }

    public void setOriginalUrl(String originalUrl) {
        this.origin_url = originalUrl;
    }
}
