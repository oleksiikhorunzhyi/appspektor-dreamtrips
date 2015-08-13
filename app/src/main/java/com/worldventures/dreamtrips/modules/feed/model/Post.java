package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import java.io.Serializable;

public class Post implements Serializable {

    private String text;
    private String filePath;

    private UploadTask uploadTask;

    private int pidType;
    private String photoCapturingFilePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setPidType(int pidType) {
        this.pidType = pidType;
    }

    public int getPidType() {
        return pidType;
    }

    public String getPhotoCapturingFilePath() {
        return photoCapturingFilePath;
    }

    public void setPhotoCapturingFilePath(String photoCapturingFilePath) {
        this.photoCapturingFilePath = photoCapturingFilePath;
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
