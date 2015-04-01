package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class Image extends BaseEntity implements Parcelable {

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private ImageVersion original;
    private ImageVersion medium;
    private ImageVersion thumb;

    public Image() {
    }

    private Image(Parcel in) {
        this.original = in.readParcelable(ImageVersion.class.getClassLoader());
        this.medium = in.readParcelable(ImageVersion.class.getClassLoader());
        this.thumb = in.readParcelable(ImageVersion.class.getClassLoader());
    }

    public ImageVersion getOriginal() {
        return original;
    }

    public void setOriginal(ImageVersion original) {
        this.original = original;
    }

    public ImageVersion getMedium() {
        return medium;
    }

    public void setMedium(ImageVersion medium) {
        this.medium = medium;
    }

    public ImageVersion getThumb() {
        return thumb;
    }

    //thumb medium
    public void setThumb(ImageVersion thumb) {
        this.thumb = thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.original, flags);
        dest.writeParcelable(this.medium, flags);
        dest.writeParcelable(this.thumb, flags);
    }

    public static class ImageVersion implements Parcelable {

        public static final Creator<ImageVersion> CREATOR = new Creator<ImageVersion>() {
            public ImageVersion createFromParcel(Parcel source) {
                return new ImageVersion(source);
            }

            public ImageVersion[] newArray(int size) {
                return new ImageVersion[size];
            }
        };
        protected String url;

        public ImageVersion() {
        }

        private ImageVersion(Parcel in) {
            this.url = in.readString();
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
        }
    }
}
