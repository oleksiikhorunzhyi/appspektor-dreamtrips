package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.kbeanie.imagechooser.api.ChosenImage;

import java.util.ArrayList;
import java.util.List;

public class CreatePostBundle implements Parcelable {

    private List<ChosenImage> images;
    private int imageType;

    public CreatePostBundle(List<ChosenImage> images, int imageType) {
        this.images = images;
        this.imageType = imageType;
    }

    public List<ChosenImage> getImages() {
        return images == null ? new ArrayList<>() : images;
    }

    public int getImageType() {
        return imageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.images);
        dest.writeInt(this.imageType);
    }

    protected CreatePostBundle(Parcel in) {
        this.images = new ArrayList<>();
        in.readList(this.images, List.class.getClassLoader());
        this.imageType = in.readInt();
    }

    public static final Creator<CreatePostBundle> CREATOR = new Creator<CreatePostBundle>() {
        public CreatePostBundle createFromParcel(Parcel source) {
            return new CreatePostBundle(source);
        }

        public CreatePostBundle[] newArray(int size) {
            return new CreatePostBundle[size];
        }
    };
}
