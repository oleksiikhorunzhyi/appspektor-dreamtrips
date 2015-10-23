package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Photo extends BaseFeedEntity implements IFullScreenObject {

    private String title;
    private Date shotAt;
    private Location location;
    private List<String> tags;
    private Image images;
    private String taskId;

    public Photo() {
    }

    @Override
    public String place() {
        return location != null ? location.getName() : null;
    }

    public String getFsId() {
        return uid;
    }

    public Date getShotAt() {
        return shotAt;
    }

    public void setShotAt(Date shotAt) {
        this.shotAt = shotAt;
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
                ", owner=" + owner +
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
        if (owner != null) {
            return owner.getUsername();
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
        return commentsCount;
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
        if (owner == null) {
            return "";
        } else {
            return owner.getAvatar().getMedium();
        }
    }

    @Override
    public User getUser() {
        return owner;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

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
        parcel.writeSerializable(shotAt);
        parcel.writeParcelable(location, i);
        parcel.writeStringList(tags);
        parcel.writeParcelable(images, i);
        parcel.writeString(taskId);
        parcel.writeParcelable(owner, i);
    }

    protected Photo(Parcel in) {
        uid = in.readString();
        commentsCount = in.readInt();
        likesCount = in.readInt();
        liked = in.readInt() == 1;
        title = in.readString();
        shotAt = (Date) in.readSerializable();
        location = in.readParcelable(Location.class.getClassLoader());
        tags = in.createStringArrayList();
        images = in.readParcelable(Image.class.getClassLoader());
        taskId = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
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
}
