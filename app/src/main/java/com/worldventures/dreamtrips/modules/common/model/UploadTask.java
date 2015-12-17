package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.util.ArrayList;
import java.util.Date;

public class UploadTask implements IFullScreenObject {

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

    private Module module;

    private String linkedItemId;

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
        linkedItemId = in.readString();
    }

    public static final Creator<UploadTask> CREATOR = new Creator<UploadTask>() {
        @Override
        public UploadTask createFromParcel(Parcel in) {
            return new UploadTask(in);
        }

        @Override
        public UploadTask[] newArray(int size) {
            return new UploadTask[size];
        }
    };

    public void changed(UploadTask newTask) {
        amazonTaskId = newTask.getAmazonTaskId();
        bucketName = newTask.getBucketName();
        key = newTask.getKey();
        status = newTask.getStatus();
        originUrl = newTask.getOriginUrl();
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
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

        return !(filePath != null ? !filePath.equals(that.filePath) : that.filePath != null);
    }

    public String getLinkedItemId() {
        return linkedItemId;
    }

    public void setLinkedItemId(String linkedItemId) {
        this.linkedItemId = linkedItemId;
    }

    @Override
    public int hashCode() {
        return filePath != null ? filePath.hashCode() : 0;
    }

    @Override
    public String getImagePath() {
        return getFilePath();
    }

    @Override
    public Image getFSImage() {
        Image image = new Image();
        image.setUrl(getFilePath());
        image.setFromFile(true);
        return image;
    }

    @Override
    public String getFSTitle() {
        return null;
    }

    @Override
    public String getFsDescription() {
        return title;
    }

    @Override
    public String getFsShareText() {
        return title;
    }

    @Override
    public int getFsCommentCount() {
        return -1;
    }

    @Override
    public int getFsLikeCount() {
        return 0;
    }

    @Override
    public String getFsLocation() {
        return locationName;
    }

    @Override
    public String getFsDate() {
        return "";
    }

    @Override
    public String getFsUserPhoto() {
        return null;
    }

    @Override
    public String getFsId() {
        return amazonTaskId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filePath);
        parcel.writeInt(progress);
        parcel.writeString(amazonTaskId);
        parcel.writeString(bucketName);
        parcel.writeString(key);
        parcel.writeStringList(tags);
        parcel.writeString(title);
        parcel.writeString(locationName);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(originUrl);
        parcel.writeString(type);
        parcel.writeString(linkedItemId);
    }

    @Override
    public User getUser() {
        return null;
    }

    public enum Status {
        COMPLETED, CANCELED, IN_PROGRESS, FAILED
    }

    public enum Module {
        BUCKET, IMAGES, POST
    }

}
