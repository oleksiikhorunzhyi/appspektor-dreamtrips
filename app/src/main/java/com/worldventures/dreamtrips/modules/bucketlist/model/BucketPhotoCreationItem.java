package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.techery.janet.ActionState;

public class BucketPhotoCreationItem implements Parcelable, Serializable {

    private String filePath;
    private String originUrl = "";
    private String bucketId;
    private ActionState.Status status;

    public BucketPhotoCreationItem() {
    }

    public String getFilePath() {
        return filePath;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getBucketId() {
        return bucketId;
    }

    public ActionState.Status getStatus() {
        return status;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public void setStatus(ActionState.Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketPhotoCreationItem that = (BucketPhotoCreationItem) o;

        return filePath != null ? filePath.equals(that.filePath) : that.filePath == null;

    }

    @Override
    public int hashCode() {
        return filePath != null ? filePath.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.originUrl);
        dest.writeString(this.bucketId);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected BucketPhotoCreationItem(Parcel in) {
        this.filePath = in.readString();
        this.originUrl = in.readString();
        this.bucketId = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : ActionState.Status.values()[tmpStatus];
    }

    public static final Creator<BucketPhotoCreationItem> CREATOR = new Creator<BucketPhotoCreationItem>() {
        @Override
        public BucketPhotoCreationItem createFromParcel(Parcel source) {
            return new BucketPhotoCreationItem(source);
        }

        @Override
        public BucketPhotoCreationItem[] newArray(int size) {
            return new BucketPhotoCreationItem[size];
        }
    };
}
