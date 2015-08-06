package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class UploadTask implements Serializable {

    protected static final long serialVersionUID = 1233322;

    private String filePath;
    private int progress;
    private Status status;
    private String amazonTaskId;
    private String bucketName;
    private String key;

    private ArrayList<String> tags;

    private String title;

    private String locationName;
    private float latitude;
    private float longitude;
    private Date shotAt;

    private String originUrl;

    private String type;

    public UploadTask() {
    }

    protected UploadTask(Parcel in) {
        filePath = in.readString();
        progress = in.readInt();
        amazonTaskId = in.readString();
        bucketName = in.readString();
        key = in.readString();
        tags = in.createStringArrayList();
        title = in.readString();
        locationName = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        originUrl = in.readString();
        type = in.readString();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAmazonTaskId() {
        return amazonTaskId;
    }

    public void setAmazonTaskId(String amazonTaskId) {
        this.amazonTaskId = amazonTaskId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadTask that = (UploadTask) o;

        return !(amazonTaskId != null ? !amazonTaskId.equals(that.amazonTaskId) : that.amazonTaskId != null);
    }

    @Override
    public int hashCode() {
        return amazonTaskId != null ? amazonTaskId.hashCode() : 0;
    }

    public enum Status {
        COMPLETED, CANCELED, IN_PROGRESS, FAILED
    }
}
