package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class Inspiration extends BaseEntity implements IFullScreenAvailableObject, Parcelable {

    public static final Creator<Inspiration> CREATOR = new Creator<Inspiration>() {
        public Inspiration createFromParcel(Parcel source) {
            return new Inspiration(source);
        }

        public Inspiration[] newArray(int size) {
            return new Inspiration[size];
        }
    };

    private Image images;
    private String quote;
    private String author;

    public Inspiration() {
    }

    private Inspiration(Parcel in) {
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.quote = in.readString();
        this.author = in.readString();
        this.id = in.readInt();
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public Image getFSImage() {
        return images;
    }

    @Override
    public String getFSTitle() {
        return author;
    }

    @Override
    public String getFsDescription() {
        return quote;
    }

    @Override
    public String getFsShareText() {
        return quote + " -" + author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.images, 0);
        dest.writeString(this.quote);
        dest.writeString(this.author);
        dest.writeInt(this.id);
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
    public String getUserName() {
        return author;
    }

    @Override
    public String getUserLocation() {
        return "";
    }

    @Override
    public String getUserAvatar() {
        return "";
    }

}
