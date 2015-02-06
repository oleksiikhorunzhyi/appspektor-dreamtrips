package com.worldventures.dreamtrips.core.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Photo extends BaseEntity implements Parcelable, IFullScreenAvailableObject {


    String title;
    String shotAt;
    Location location;
    List<String> tags;
    Image images;
    boolean liked;
    int likeCount;
    String taskId;
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Location getCoordinates() {
        return location;
    }

    public void setCoordinates(Location coordinates) {
        this.location = coordinates;
    }

    public String getShotAt() {
        return shotAt;
    }

    public void setShotAt(String shotAt) {
        this.shotAt = shotAt;
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
        return "Photo{" +
                "title='" + title + '\'' +
                ", shotAt='" + shotAt + '\'' +
                ", location=" + location +
                ", tags=" + tags +
                ", images=" + images +
                ", liked=" + liked +
                ", likeCount=" + likeCount +
                ", taskId='" + taskId + '\'' +
                ", user=" + user +
                '}';
    }

    public Photo() {
    }


    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.shotAt);
        dest.writeParcelable(this.location, 0);
        dest.writeList(this.tags);
        dest.writeParcelable(this.images, 0);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likeCount);
        dest.writeString(this.taskId);
        dest.writeParcelable(this.user, 0);
        dest.writeInt(this.id);
    }

    private Photo(Parcel in) {
        this.title = in.readString();
        this.shotAt = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.tags = new ArrayList<String>();
        in.readList(this.tags, String.class.getClassLoader());
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.liked = in.readByte() != 0;
        this.likeCount = in.readInt();
        this.taskId = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.id = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public Image getFSImage() {
        return images;
    }

    @Override
    public String getFSTitle() {
        return title;
    }

    @Override
    public String getFsDescription() {
        return "";
    }
}
