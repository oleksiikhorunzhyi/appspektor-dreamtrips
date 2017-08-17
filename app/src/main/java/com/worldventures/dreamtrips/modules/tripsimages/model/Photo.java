package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Photo extends BaseFeedEntity implements ImagePathHolder, Parcelable {

   private String title;
   private Date shotAt;
   private Date createdAt;
   private Location location;
   private List<String> tags;
   private String url;
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

   /*
    * Can be null if we get photo as attachment from feed
    * There are complications to add createdAt in feed on server
    */
   @Nullable
   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
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

   public void setUrl(String url) {
      this.url = url;
   }

   public List<PhotoTag> getPhotoTags() {
      if (photoTags == null) photoTags = new ArrayList<>();
      return photoTags;
   }

   public void setPhotoTags(List<PhotoTag> photoTags) {
      this.photoTags = photoTags;
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
   public String getOriginalText() {
      return getTitle();
   }

   @Override
   public String getImagePath() {
      return url;
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
      parcel.writeSerializable(createdAt);
      parcel.writeParcelable(location, i);
      parcel.writeStringList(tags);
      parcel.writeString(url);
      parcel.writeParcelable(owner, i);
      parcel.writeTypedList(photoTags);
      parcel.writeInt(photoTagsCount);
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
      createdAt = (Date) in.readSerializable();
      location = in.readParcelable(Location.class.getClassLoader());
      tags = in.createStringArrayList();
      url = in.readString();
      owner = in.readParcelable(User.class.getClassLoader());
      photoTags = new ArrayList<>();
      in.readTypedList(photoTags, PhotoTag.CREATOR);
      photoTagsCount = in.readInt();
      width = in.readInt();
      height = in.readInt();
   }
}
