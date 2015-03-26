package com.worldventures.dreamtrips.modules.tripsimages.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.List;

public class Photo extends BaseEntity implements Parcelable, IFullScreenAvailableObject {


    private String title;
    private DateTime shotAt;
    private Location location;
    private List<String> tags;
    private Image images;
    private boolean liked;
    private int likesCount;
    private String taskId;
    private User user;

    public Photo() {
    }


    public DateTime getShotAt() {
        return shotAt;
    }

    public void setShotAt(DateTime shotAt) {
        this.shotAt = shotAt;
    }

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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
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
                ", likesCount=" + likesCount +
                ", taskId='" + taskId + '\'' +
                ", user=" + user +
                '}';
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Image getFSImage() {
        return images;
    }

    @Override
    public String getFSTitle() {
        if (user != null) {
            return user.getFullName();
        }
        return "";
    }

    @Override
    public String getFsDescription() {
        return title;
    }

    @Override
    public String getFsShareText() {
        return title;
    }

    @Override
    public String getPhotoLocation() {
        return location != null ? location.getName() : "";
    }

    @Override
    public int getFsCommentCount() {
        return -1;
    }

    @Override
    public int getFsLikeCount() {
        return likesCount;
    }

    @Override
    public String getFsLocation() {
        if (location == null) {
            return "";
        }
        return location.getName();
    }

    @Override
    public String getFsDate() {
        if (shotAt == null) {
            return "";
        }
        return DateTimeUtils.convertDateToString(shotAt, DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT);
    }

    @Override
    public String getUserName() {
        return user != null ? user.getUsername() : "";
    }

    @Override
    public String getUserLocation() {
        return user != null ? user.getLocation() : "";
    }

    @Override
    public String getUserAvatar() {
        return user != null ? user.getAvatar().getThumb() : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(shotAt != null ? shotAt.getTime() : -1);
        dest.writeParcelable(this.location, 0);
        dest.writeList(this.tags);
        dest.writeParcelable(this.images, 0);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likesCount);
        dest.writeString(this.taskId);
        dest.writeParcelable(this.user, 0);
        dest.writeInt(this.id);
    }

    private Photo(Parcel in) {
        this.title = in.readString();
        long tmpShotAt = in.readLong();
        this.shotAt = tmpShotAt == -1 ? null : new DateTime(tmpShotAt);
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.tags = new ArrayList<>();
        in.readList(this.tags, ArrayList.class.getClassLoader());
        this.images = in.readParcelable(Image.class.getClassLoader());
        this.liked = in.readByte() != 0;
        this.likesCount = in.readInt();
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
}