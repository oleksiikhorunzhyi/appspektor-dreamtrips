package com.worldventures.dreamtrips.modules.feed.model;


public class PhotoGalleryModel {

    private String originalPath;
    private String thumbnailPath;
    private boolean checked;

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoGalleryModel that = (PhotoGalleryModel) o;

        return originalPath.equals(that.originalPath);

    }

    @Override
    public int hashCode() {
        return originalPath.hashCode();
    }
}
