package com.worldventures.dreamtrips.modules.common.model;


import android.os.Parcel;
import android.os.Parcelable;

public class PhotoGalleryModel implements Parcelable, BasePhotoPickerModel {

    private String originalPath;
    private String thumbnailPath;
    private boolean checked;

    public PhotoGalleryModel(String originalPath) {
        this.originalPath = originalPath;
        this.thumbnailPath = "file://" + this.originalPath;
    }

    protected PhotoGalleryModel(Parcel in) {
        originalPath = in.readString();
        thumbnailPath = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<PhotoGalleryModel> CREATOR = new Creator<PhotoGalleryModel>() {
        @Override
        public PhotoGalleryModel createFromParcel(Parcel in) {
            return new PhotoGalleryModel(in);
        }

        @Override
        public PhotoGalleryModel[] newArray(int size) {
            return new PhotoGalleryModel[size];
        }
    };

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
        dest.writeString(originalPath);
        dest.writeString(thumbnailPath);
        dest.writeByte((byte) (checked ? 1 : 0));
    }
}
