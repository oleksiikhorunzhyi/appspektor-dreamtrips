package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
   private List<PhotoTag> photoTags;
   private int photoTagsCount;
   private int width;
   private int height;

   public Photo() {
   }

   public Photo(String uid) {
      this.uid = uid;
   }

   @Override
   public String place() {
      return location != null ? location.getName() : null;
   }

   public Date getShotAt() {
      return shotAt;
   }

   public void setShotAt(Date shotAt) {
      this.shotAt = shotAt;
   }

   public List<String> getTags() {
      if (tags == null) tags = new ArrayList<>();
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

   @NotNull
   public String getTitle() {
      return title != null ? title : "";
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

   public List<PhotoTag> getPhotoTags() {
      if (photoTags == null) photoTags = new ArrayList<>();
      return photoTags;
   }

   public void setPhotoTags(List<PhotoTag> photoTags) {
      if (photoTags != null) {
         this.photoTags = photoTags;
         this.photoTagsCount = photoTags.size();
      }
   }

   public int getPhotoTagsCount() {
      return photoTagsCount;
   }

   public void setPhotoTagsCount(int photoTagsCount) {
      this.photoTagsCount = photoTagsCount;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   @Override
   public String toString() {
      return "Photo{" +
            "title='" + title + '\'' +
            ", shotAt=" + shotAt +
            ", location=" + location +
            ", tags=" + tags +
            ", images=" + images +
            ", taskId='" + taskId + '\'' +
            ", photoTags=" + photoTags +
            ", photoTagsCount=" + photoTagsCount +
            ", width=" + width +
            ", height=" + height +
            '}';
   }

   public String getTaskId() {
      return taskId;
   }

   public void setTaskId(String taskId) {
      this.taskId = taskId;
   }

   @Override
   public String getImagePath() {
      return images.getUrl();
   }

   public String getFSId() {
      return uid;
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
   public String getFSDescription() {
      return title;
   }

   @Override
   public String getFSShareText() {
      return title;
   }

   @Override
   public int getFSCommentCount() {
      return commentsCount;
   }

   @Override
   public int getFSLikeCount() {
      return getLikesCount();
   }

   @Override
   public String getFSLocation() {
      if (location == null) {
         return "";
      }
      return location.getName();
   }

   @Override
   public String getFSDate() {
      return DateTimeUtils.convertDateToString(shotAt, DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT);
   }

   @Override
   public String getFSUserPhoto() {
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

   public void setUser(User user) {
      owner = user;
   }

   public Location getLocation() {
      return location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

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
      parcel.writeTypedList(photoTags);
      parcel.writeInt(width);
      parcel.writeInt(height);
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
      photoTags = new ArrayList<>();
      in.readTypedList(photoTags, PhotoTag.CREATOR);
      width = in.readInt();
      height = in.readInt();
   }
}
