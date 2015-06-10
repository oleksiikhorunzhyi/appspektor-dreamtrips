package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

public class Inspiration implements IFullScreenObject, Parcelable {

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
    private String id;


    public Inspiration() {
    }

    private Inspiration(Parcel in) {
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.quote = in.readString();
        this.author = in.readString();
        this.id = in.readString();
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
        return String.format("\"%s\"", quote);
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
        dest.writeString(this.id);
    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public String getFsId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

 }
