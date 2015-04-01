package com.worldventures.dreamtrips.modules.reptools.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class SuccessStory extends BaseEntity implements Parcelable, Filterable {

    public static final Creator<SuccessStory> CREATOR = new Creator<SuccessStory>() {
        public SuccessStory createFromParcel(Parcel source) {
            return new SuccessStory(source);
        }

        public SuccessStory[] newArray(int size) {
            return new SuccessStory[size];
        }
    };

    private String author;
    private String category;
    private String locale;
    private String published_date;
    private String url;
    private String sharingUrl = "";
    private boolean liked;

    public SuccessStory() {
    }

    private SuccessStory(Parcel in) {
        this.author = in.readString();
        this.category = in.readString();
        this.locale = in.readString();
        this.published_date = in.readString();
        this.url = in.readString();
        this.sharingUrl = in.readString();
        this.liked = in.readByte() != 0;
        this.id = in.readInt();
    }

    public String getSharingUrl() {
        return sharingUrl;
    }

    public void setSharingUrl(String sharingUrl) {
        this.sharingUrl = sharingUrl;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

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
    public boolean containsQuery(String query) {
        return query == null || author.toLowerCase().contains(query);
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
        dest.writeString(this.sharingUrl);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }
}
