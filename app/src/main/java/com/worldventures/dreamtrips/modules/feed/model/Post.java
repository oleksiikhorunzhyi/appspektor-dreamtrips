package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.io.Serializable;

public class Post implements Serializable {

    private String text;
    private int imageUploadTaskId;

    private UploadTask uploadTask;

    private int pidType;

    public void setImageUploadTaskId(int imageUploadTaskId) {
        this.imageUploadTaskId = imageUploadTaskId;
    }

    public int getImageUploadTaskId() {
        return imageUploadTaskId;
    }

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

    public UploadTask getUploadTask() {
        return uploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

}
