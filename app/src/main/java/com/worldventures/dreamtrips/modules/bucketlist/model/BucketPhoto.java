package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import java.io.Serializable;

public class BucketPhoto extends BaseEntity implements Serializable, IFullScreenObject, android.os.Parcelable {

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

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public String getFsId() {
        return String.format("%d", super.getId());
    }

    @Override
    public Image getFSImage() {
        Image image = new Image();
        image.setUrl(url);
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
    public User getUser() {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originUrl);
        dest.writeString(this.url);
        dest.writeInt(this.taskId);
        dest.writeInt(this.id);
    }
}
