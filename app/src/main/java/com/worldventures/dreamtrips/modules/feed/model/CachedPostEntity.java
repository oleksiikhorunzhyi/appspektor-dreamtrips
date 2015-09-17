package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public class CachedPostEntity implements Parcelable {

    private String text;
    private String filePath;

    private UploadTask uploadTask;

    public CachedPostEntity() {
    }

    protected CachedPostEntity(Parcel in) {
        text = in.readString();
        filePath = in.readString();
        uploadTask = in.readParcelable(UploadTask.class.getClassLoader());
    }

    public static final Creator<CachedPostEntity> CREATOR = new Creator<CachedPostEntity>() {
        @Override
        public CachedPostEntity createFromParcel(Parcel in) {
            return new CachedPostEntity(in);
        }

        @Override
        public CachedPostEntity[] newArray(int size) {
            return new CachedPostEntity[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(filePath);
        dest.writeParcelable(uploadTask, flags);
    }
}
