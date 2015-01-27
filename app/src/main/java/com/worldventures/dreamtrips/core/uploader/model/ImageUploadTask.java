package com.worldventures.dreamtrips.core.uploader.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class ImageUploadTask extends RealmObject {

    @Index
    private String taskId;

    private String fileUri;
    private float progress;

    private String title;
    private String locationName;
    private float latitude;
    private float longitude;
    private Date shotAt;
    private String originUrl;

    /**
     * Temporary for fix RealmDB null object problem
     */
    public static ImageUploadTask copy(ImageUploadTask obj) {
        ImageUploadTask t = new ImageUploadTask();
        t.setTaskId(obj.getTaskId());
        t.setFileUri(obj.getFileUri());
        t.setProgress(obj.getProgress());
        t.setTitle(obj.getTitle());
        t.setLocationName(obj.getLocationName());
        t.setLatitude(obj.getLatitude());
        t.setLongitude(obj.getLongitude());
        t.setShotAt(obj.getShotAt());
        t.setOriginUrl(obj.getOriginUrl());
        return t;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Date getShotAt() {
        return shotAt;
    }

    public void setShotAt(Date shotAt) {
        this.shotAt = shotAt;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }
}
