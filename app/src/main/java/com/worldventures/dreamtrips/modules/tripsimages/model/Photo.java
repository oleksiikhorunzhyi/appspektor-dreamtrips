package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedObject;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Photo extends BaseFeedObject implements IFullScreenObject {

    private String title;
    private Date shotAt;
    private Location location;
    private List<String> tags;
    private Image images;
    private String taskId;

    public Photo() {
    }

    protected Photo(Parcel in) {
        uid = in.readString();
        commentsCount = in.readInt();
        likesCount = in.readInt();
        liked = in.readInt() == 1;
        title = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        tags = in.createStringArrayList();
        images = in.readParcelable(Image.class.getClassLoader());
        taskId = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getFsId() {
        return uid;
    }

    public Date getShotAt() {
        return shotAt;
    }

    public void setShotAt(Date shotAt) {
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

    @Override
    public String toString() {
        return "Photo{" +
                "title='" + title + '\'' +
                ", shotAt='" + shotAt + '\'' +
                ", location=" + location +
                ", tags=" + tags +
                ", images=" + images +
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
            return user.getUsername();
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
    public int getFsCommentCount() {
        return -1;
    }

    @Override
    public int getFsLikeCount() {
        return getLikesCount();
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
        return DateTimeUtils.convertDateToString(shotAt, DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT);
    }

    @Override
    public String getFsUserPhoto() {
        if (user == null) {
            return "";
        } else {
            return user.getAvatar().getMedium();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeInt(commentsCount);
        parcel.writeInt(likesCount);
        parcel.writeInt(liked ? 1 : 0);
        parcel.writeString(title);
        parcel.writeParcelable(location, i);
        parcel.writeStringList(tags);
        parcel.writeParcelable(images, i);
        parcel.writeString(taskId);
        parcel.writeParcelable(user, i);
    }
}
