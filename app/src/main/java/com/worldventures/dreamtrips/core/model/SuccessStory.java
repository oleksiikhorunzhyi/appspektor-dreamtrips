package com.worldventures.dreamtrips.core.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SuccessStory extends BaseEntity implements Parcelable {

    String author = "aut";
    String category = "cat";
    String locale;
    String published_date;
    String url = "http://google.com.ua";

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
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
        dest.writeString(this.author);
        dest.writeString(this.category);
        dest.writeString(this.locale);
        dest.writeString(this.published_date);
        dest.writeString(this.url);
        dest.writeInt(this.id);
    }

    public SuccessStory() {
    }

    private SuccessStory(Parcel in) {
        this.author = in.readString();
        this.category = in.readString();
        this.locale = in.readString();
        this.published_date = in.readString();
        this.url = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<SuccessStory> CREATOR = new Creator<SuccessStory>() {
        public SuccessStory createFromParcel(Parcel source) {
            return new SuccessStory(source);
        }

        public SuccessStory[] newArray(int size) {
            return new SuccessStory[size];
        }
    };
}
