package com.worldventures.dreamtrips.core.model;

import android.net.Uri;

import com.worldventures.dreamtrips.BuildConfig;

public class Image extends BaseEntity {
    String original;
    String medium;
    String thumb;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "original='" + original + '\'' +
                ", medium='" + medium + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }

    public Uri getMediumUri() {
        return Uri.parse(BuildConfig.DreamTripsApi + getMedium());
    }
}
