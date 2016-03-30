package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;

import java.util.ArrayList;
import java.util.List;

public class CreateEntityBundle implements Parcelable {

    private List<PhotoGalleryModel> images;
    private int imageType;

    private boolean showPickerImmediately;

    public CreateEntityBundle(boolean showPickerImmediately) {
        this.showPickerImmediately = showPickerImmediately;
        this.images = new ArrayList<>();
    }

    public CreateEntityBundle(List<PhotoGalleryModel> images, int imageType) {
        this.images = images;
        this.imageType = imageType;
    }

    public List<PhotoGalleryModel> getImages() {
        return images == null ? new ArrayList<>() : images;
    }

    public int getImageType() {
        return imageType;
    }

    public boolean isShowPickerImmediately() {
        return showPickerImmediately;
    }

    public void setShowPickerImmediately(boolean showPickerImmediately) {
        this.showPickerImmediately = showPickerImmediately;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(images);
        dest.writeInt(this.imageType);
    }

    protected CreateEntityBundle(Parcel in) {
        this.images = new ArrayList<>();
        in.readList(images, PhotoGalleryModel.class.getClassLoader());
        this.imageType = in.readInt();
    }

    public static final Creator<CreateEntityBundle> CREATOR = new Creator<CreateEntityBundle>() {
        public CreateEntityBundle createFromParcel(Parcel source) {
            return new CreateEntityBundle(source);
        }

        public CreateEntityBundle[] newArray(int size) {
            return new CreateEntityBundle[size];
        }
    };

}
