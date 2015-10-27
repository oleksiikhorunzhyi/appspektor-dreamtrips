package com.worldventures.dreamtrips.modules.feed.model;


public class PhotoGalleryModel {

    private String originalPath;
    private String thumbnailPath;

    public PhotoGalleryModel(String originalPath) {
        this.originalPath = originalPath;
        this.thumbnailPath = "file://" + this.originalPath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
