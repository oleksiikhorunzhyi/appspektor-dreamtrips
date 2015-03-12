package com.worldventures.dreamtrips.core.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image extends BaseEntity implements Parcelable{



    public static class ImageVersion implements Parcelable{

        String url;

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

        public ImageVersion() {
        }

        private ImageVersion(Parcel in) {
            this.url = in.readString();
        }

        public static final Creator<ImageVersion> CREATOR = new Creator<ImageVersion>() {
            public ImageVersion createFromParcel(Parcel source) {
                return new ImageVersion(source);
            }

            public ImageVersion[] newArray(int size) {
                return new ImageVersion[size];
            }
        };
    }

    ImageVersion original;
    ImageVersion medium;
    ImageVersion thumb;

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

    public Image() {
    }

    private Image(Parcel in) {
        this.original = in.readParcelable(ImageVersion.class.getClassLoader());
        this.medium = in.readParcelable(ImageVersion.class.getClassLoader());
        this.thumb = in.readParcelable(ImageVersion.class.getClassLoader());
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
