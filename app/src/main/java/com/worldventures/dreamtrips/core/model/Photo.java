package com.worldventures.dreamtrips.core.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Photo extends BaseEntity implements Parcelable {


    String title;
    int userId;
    String shotAt;
    String locationName;
    Coordinate coordinates;
    List<String> tags;
    Image images;
    boolean liked;
    int likeCount;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getShotAt() {
        return shotAt;
    }

    public void setShotAt(String shotAt) {
        this.shotAt = shotAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", userId=" + userId +
                ", shotAt='" + shotAt + '\'' +
                ", locationName='" + locationName + '\'' +
                ", coordinates=" + coordinates +
                ", tags=" + tags +
                ", url=" + images +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.userId);
        dest.writeString(this.shotAt);
        dest.writeString(this.locationName);
        dest.writeParcelable(this.coordinates, flags);
        dest.writeList(this.tags);
        dest.writeParcelable(this.images, flags);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likeCount);
    }

    public Photo() {
    }

    private Photo(Parcel in) {
        this.title = in.readString();
        this.userId = in.readInt();
        this.shotAt = in.readString();
        this.locationName = in.readString();
        this.coordinates = in.readParcelable(Coordinate.class.getClassLoader());
        this.tags = new ArrayList<>();
        in.readList(this.tags, ArrayList.class.getClassLoader());
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.liked = in.readByte() != 0;
        this.likeCount = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
