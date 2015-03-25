package com.worldventures.dreamtrips.modules.tripsimages.uploader;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class ImageUploadTask implements Serializable, IFullScreenAvailableObject, Parcelable {

    public static final Creator<ImageUploadTask> CREATOR = new Creator<ImageUploadTask>() {
        public ImageUploadTask createFromParcel(Parcel source) {
            return new ImageUploadTask(source);
        }

        public ImageUploadTask[] newArray(int size) {
            return new ImageUploadTask[size];
        }
    };
    private String taskId;
    private String fileUri;
    private float progress;
    private String title;
    private String locationName;
    private float latitude;
    private float longitude;
    private Date shotAt;
    private String originUrl;
    private ArrayList<String> tags;
    private boolean failed;

    public ImageUploadTask() {
    }

    private ImageUploadTask(Parcel in) {
        this.taskId = in.readString();
        this.fileUri = in.readString();
        this.progress = in.readFloat();
        this.title = in.readString();
        this.locationName = in.readString();
        this.latitude = in.readFloat();
        this.longitude = in.readFloat();
        long tmpShotAt = in.readLong();
        this.shotAt = tmpShotAt == -1 ? null : new Date(tmpShotAt);
        this.originUrl = in.readString();
        this.tags = (ArrayList<String>) in.readSerializable();
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

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public Image getFSImage() {
        Image image = new Image();
        Image.ImageVersion version = new Image.ImageVersion();
        version.setUrl(getFileUri());
        image.setMedium(version);
        image.setOriginal(version);
        image.setThumb(version);
        return image;
    }

    @Override
    public String getFSTitle() {
        return title;
    }

    @Override
    public String getFsDescription() {
        return "";
    }

    @Override
    public String getFsShareText() {
        return "";
    }

    @Override
    public String getPhotoLocation() {
        return "";
    }

    @Override
    public int getFsCommentCount() {
        return -1;
    }

    @Override
    public int getFsLikeCount() {
        return -1;
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
    public String getUserName() {
        return "";
    }

    @Override
    public String getUserLocation() {
        return locationName;
    }

    @Override
    public String getUserAvatar() {
        return "";
    }

    @Override
    public int getId() {
        return taskId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.taskId);
        dest.writeString(this.fileUri);
        dest.writeFloat(this.progress);
        dest.writeString(this.title);
        dest.writeString(this.locationName);
        dest.writeFloat(this.latitude);
        dest.writeFloat(this.longitude);
        dest.writeLong(shotAt != null ? shotAt.getTime() : -1);
        dest.writeString(this.originUrl);
        dest.writeSerializable(this.tags);
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}
