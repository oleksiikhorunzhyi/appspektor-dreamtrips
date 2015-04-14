package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.io.Serializable;

public class BucketPhoto extends BaseEntity implements Serializable, IFullScreenAvailableObject, android.os.Parcelable {

    public static final long serialVersionUID = 14534647;
    public static final Creator<BucketPhoto> CREATOR = new Creator<BucketPhoto>() {
        public BucketPhoto createFromParcel(Parcel source) {
            return new BucketPhoto(source);
        }

        public BucketPhoto[] newArray(int size) {
            return new BucketPhoto[size];
        }
    };
    @SerializedName("origin_url")
    private String originUrl;
    private String url;
    private int taskId;

    public BucketPhoto() {
    }

    private BucketPhoto(Parcel in) {
        this.originUrl = in.readString();
        this.url = in.readString();
        this.taskId = in.readInt();
        this.id = in.readInt();
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getFsId() {
        return String.format("%d", super.getId());
    }

    public String getThumbUrl() {
        String args = String.format(UniversalImageLoader.PATTERN, 256, 256);
        return String.format("%s%s", getUrl(), args);
    }
    public String getMedium() {
        String args = String.format(UniversalImageLoader.PATTERN, 720, 720);
        return String.format("%s%s", getUrl(), args);
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String url) {
        this.originUrl = url;
    }

    @Override
    public Image getFSImage() {
        Image image = new Image();
        Image.ImageVersion version = new Image.ImageVersion();
        version.setUrl(getMedium());
        image.setMedium(version);
        version = new Image.ImageVersion();
        version.setUrl(getOriginUrl());
        image.setOriginal(version);
        version = new Image.ImageVersion();
        version.setUrl(getThumbUrl());
        image.setThumb(version);
        return image;
    }

    @Override
    public String getFSTitle() {
        return "";
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
    public String getUserName() {
        return "";
    }

    @Override
    public String getUserLocation() {
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
        return "";
    }

    @Override
    public String getFsDate() {
        return "";
    }

    @Override
    public String getFsUserPhoto() {
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originUrl);
        dest.writeString(this.url);
        dest.writeInt(this.taskId);
        dest.writeInt(this.id);
    }
}
