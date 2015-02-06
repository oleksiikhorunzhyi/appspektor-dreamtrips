package com.worldventures.dreamtrips.core.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Inspiration extends BaseEntity implements IFullScreenAvailableObject, Parcelable {

    Image images;
    String quote;
    String author;

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
        return "\u2014 " + author;
    }

    @Override
    public String getFsDescription() {
        return "\"" + quote + "\"";
    }

    @Override
    public String getFsShareText() {
        return getFsDescription() + " " + getFSTitle();
    }


    public Inspiration() {
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

    private Inspiration(Parcel in) {
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.quote = in.readString();
        this.author = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<Inspiration> CREATOR = new Creator<Inspiration>() {
        public Inspiration createFromParcel(Parcel source) {
            return new Inspiration(source);
        }

        public Inspiration[] newArray(int size) {
            return new Inspiration[size];
        }
    };
}
