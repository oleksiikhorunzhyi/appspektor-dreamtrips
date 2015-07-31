package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.io.Serializable;

public class Post implements Serializable {

    private String text;
    private ImageUploadTask imageUploadTask;

    private String imageUploadUrl;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImageUploadTask getImageUploadTask() {
        return imageUploadTask;
    }

    public void setImageUploadTask(ImageUploadTask imageUploadTask) {
        this.imageUploadTask = imageUploadTask;
    }

    public String getImageUploadUrl() {
        return imageUploadUrl;
    }

    public void setImageUploadUrl(String imageUploadUrl) {
        this.imageUploadUrl = imageUploadUrl;
    }
}
