package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;

public class TripImage implements IFullScreenObject {

    public static final long serialVersionUID = 128L;

    private String id;
    private String description;
    private String url;
    private String type;
    @SerializedName("origin_url")
    private String originUrl;

    public String getUrl() {
        return url != null ? url : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(int width, int height) {
        return getUrl() + String.format(Image.PATTERN, width, height);
    }

    public String getType() {
        return type;
    }

    @Override
    public String getImagePath() {
        return url;
    }

    @Override
    public Image getFSImage() {
        Image image = new Image();
        image.setUrl(url);
        return image;
    }

    @Override
    public String getFSTitle() {
        return "";
    }

    @Override
    public String getFsDescription() {
        return description;
    }

    @Override
    public String getFsShareText() {
        return "";
    }

    @Override
    public String getFsId() {
        return id;
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
        return null;
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
    public User getUser() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.originUrl);
    }

    public TripImage() {
    }

    protected TripImage(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.originUrl = in.readString();
    }

    public static final Creator<TripImage> CREATOR = new Creator<TripImage>() {
        public TripImage createFromParcel(Parcel source) {
            return new TripImage(source);
        }

        public TripImage[] newArray(int size) {
            return new TripImage[size];
        }
    };
}
