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
    Image images;
    String quote;
    String author;

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
        return getFsDescription() + " " + getFSTitle();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Inspiration that = (Inspiration) o;

        if (id != that.id) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (images != null ? !images.equals(that.images) : that.images != null) return false;
        if (quote != null ? !quote.equals(that.quote) : that.quote != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (quote != null ? quote.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}
