package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.Serializable;

public class PhotoGalleryModel implements Parcelable, BasePhotoPickerModel, Serializable {

    private String originalPath;
    private String thumbnailPath;
    private boolean checked;
    private long dateTaken;
    private Size size;

    public PhotoGalleryModel(String originalPath) {
        this(originalPath, 0);
    }

    public PhotoGalleryModel(String originalPath, Size size) {
        this(originalPath);
        this.size = size;
    }

    public PhotoGalleryModel(String originalPath, long dateTaken) {
        this.originalPath = originalPath;
        this.thumbnailPath = ValidationUtils.isUrl(originalPath) ? this.originalPath : "file://" + this.originalPath;
        this.dateTaken = dateTaken;
    }

    @Override
    public String getOriginalPath() {
        return originalPath;
    }

    @Override
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    @Nullable
    public Size getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoGalleryModel that = (PhotoGalleryModel) o;

        return originalPath.equals(that.originalPath);

    }

    @Override
    public int hashCode() {
        return originalPath.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalPath);
        dest.writeString(this.thumbnailPath);
        dest.writeByte(checked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.dateTaken);
    }

    protected PhotoGalleryModel(Parcel in) {
        this.originalPath = in.readString();
        this.thumbnailPath = in.readString();
        this.checked = in.readByte() != 0;
        this.dateTaken = in.readLong();
    }

    public static final Creator<PhotoGalleryModel> CREATOR = new Creator<PhotoGalleryModel>() {
        @Override
        public PhotoGalleryModel createFromParcel(Parcel source) {
            return new PhotoGalleryModel(source);
        }

        @Override
        public PhotoGalleryModel[] newArray(int size) {
            return new PhotoGalleryModel[size];
        }
    };
}
