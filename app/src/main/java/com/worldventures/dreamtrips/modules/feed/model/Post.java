package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;

import java.io.Serializable;

public class Post implements Serializable {

    private String text;
    private ImageUploadTask imageUploadTask;

    private int pidType;

    public void setPidType(int pidType) {
        this.pidType = pidType;
    }

    public int getPidType() {
        return pidType;
    }

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

}
