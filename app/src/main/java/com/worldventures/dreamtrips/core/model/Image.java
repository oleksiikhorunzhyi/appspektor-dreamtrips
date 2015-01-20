package com.worldventures.dreamtrips.core.model;

public class Image extends BaseEntity {

    public static class ImageVersion {
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    ImageVersion original;
    ImageVersion medium;
    ImageVersion thumb;

    public ImageVersion getOriginal() {
        return original;
    }

    public void setOriginal(ImageVersion original) {
        this.original = original;
    }

    public ImageVersion getMedium() {
        return medium;
    }

    public void setMedium(ImageVersion medium) {
        this.medium = medium;
    }

    public ImageVersion getThumb() {
        return thumb;
    }

    public void setThumb(ImageVersion thumb) {
        this.thumb = thumb;
    }
}
