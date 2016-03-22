package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateEntityBundle implements Parcelable {

    private List<ChosenImage> images;
    private int imageType;

    private boolean showPickerImmediately;

    public CreateEntityBundle(boolean showPickerImmediately) {
        this.showPickerImmediately = showPickerImmediately;
        this.images = new ArrayList<>();
    }

    public CreateEntityBundle(List<ChosenImage> images, int imageType) {
        this.images = images;
        this.imageType = imageType;
    }

    public List<ChosenImage> getImages() {
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
        List<TempImage> tempImages = new ArrayList<>();
        Queryable.from(images).forEachR(image -> {
            TempImage tempImage = new TempImage();
            tempImage.filePathOriginal = image.getFilePathOriginal();
            tempImage.fileThumbnail = image.getFileThumbnail();
            tempImage.fileThumbnailSmall = image.getFileThumbnailSmall();
        });
        dest.writeList(tempImages);
        dest.writeInt(this.imageType);
    }

    protected CreateEntityBundle(Parcel in) {
        List<TempImage> tempImages = new ArrayList<>();
        in.readList(tempImages, List.class.getClassLoader());
        this.images = new ArrayList<>();
        Queryable.from(tempImages).forEachR(tempImage -> {
            ChosenImage image = new ChosenImage();
            image.setFilePathOriginal(tempImage.filePathOriginal);
            image.setFileThumbnail(tempImage.fileThumbnail);
            image.setFileThumbnailSmall(tempImage.fileThumbnailSmall);
        });
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

    class TempImage implements Serializable {
        public String filePathOriginal;
        public String fileThumbnail;
        public String fileThumbnailSmall;
    }
}
