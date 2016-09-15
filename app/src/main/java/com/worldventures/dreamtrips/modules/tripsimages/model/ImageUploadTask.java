package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class ImageUploadTask implements IFullScreenObject, Serializable, Parcelable {

   public static final Creator<ImageUploadTask> CREATOR = new Creator<ImageUploadTask>() {
      public ImageUploadTask createFromParcel(Parcel source) {
         return new ImageUploadTask(source);
      }

      public ImageUploadTask[] newArray(int size) {
         return new ImageUploadTask[size];
      }
   };

   private String fileUri;
   private float progress;
   private String title;

   private String locationName;
   private float latitude;
   private float longitude;

   private Date shotAt;
   private String originUrl;

   private ArrayList<String> tags;
   private boolean failed;
   private User user;

   private int amazonTaskId;
   private String amazonResultUrl;

   private String type;

   public ImageUploadTask() {
   }

   private ImageUploadTask(Parcel in) {
      this.amazonTaskId = in.readInt();
      this.amazonResultUrl = in.readString();
      this.fileUri = in.readString();
      this.progress = in.readFloat();
      this.title = in.readString();
      this.locationName = in.readString();
      this.latitude = in.readFloat();
      this.longitude = in.readFloat();
      long tmpShotAt = in.readLong();
      this.shotAt = tmpShotAt == -1 ? null : new Date(tmpShotAt);
      this.originUrl = in.readString();
      this.type = in.readString();
      this.tags = (ArrayList<String>) in.readSerializable();
   }

   public int getAmazonTaskId() {
      return amazonTaskId;
   }

   public void setAmazonTaskId(int amazonTaskId) {
      this.amazonTaskId = amazonTaskId;
   }

   public String getAmazonResultUrl() {
      return amazonResultUrl;
   }

   public void setAmazonResultUrl(String amazonResultUrl) {
      this.amazonResultUrl = amazonResultUrl;
   }

   public String getFileUri() {
      return fileUri;
   }

   public void setFileUri(String fileUri) {
      this.fileUri = fileUri;
   }

   public float getProgress() {
      return progress;
   }

   public void setProgress(float progress) {
      this.progress = progress;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getLocationName() {
      return locationName;
   }

   public void setLocationName(String locationName) {
      this.locationName = locationName;
   }

   public float getLatitude() {
      return latitude;
   }

   public void setLatitude(float latitude) {
      this.latitude = latitude;
   }

   public float getLongitude() {
      return longitude;
   }

   public void setLongitude(float longitude) {
      this.longitude = longitude;
   }

   public Date getShotAt() {
      return shotAt;
   }

   public void setShotAt(Date shotAt) {
      this.shotAt = shotAt;
   }

   public String getOriginUrl() {
      return originUrl;
   }

   public void setOriginUrl(String originUrl) {
      this.originUrl = originUrl;
   }

   public ArrayList<String> getTags() {
      return tags;
   }

   public void setTags(ArrayList<String> tags) {
      this.tags = tags;
   }

   @Override
   public Image getFSImage() {
      Image image = new Image();
      image.setUrl(getFileUri());
      image.setFromFile(true);
      return image;
   }

   @Override
   public String getImagePath() {
      return getFileUri();
   }

   @Override
   public String getFSTitle() {
      return user.getFullName();
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
      return -1;
   }

   @Override
   public int getFSLikeCount() {
      return 0;
   }

   @Override
   public String getFSLocation() {
      return locationName;
   }

   @Override
   public String getFSDate() {
      return "";
   }

   @Override
   public String getFSUserPhoto() {
      return user.getAvatar().getMedium();
   }

   @Override
   public String getFSId() {
      return String.valueOf(amazonTaskId);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public User getUser() {
      return null;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.amazonTaskId);
      dest.writeString(this.amazonResultUrl);
      dest.writeString(this.fileUri);
      dest.writeFloat(this.progress);
      dest.writeString(this.title);
      dest.writeString(this.locationName);
      dest.writeFloat(this.latitude);
      dest.writeFloat(this.longitude);
      dest.writeLong(shotAt != null ? shotAt.getTime() : -1);
      dest.writeString(this.originUrl);
      dest.writeString(this.type);
      dest.writeSerializable(this.tags);
   }

   public boolean isFailed() {
      return failed;
   }

   public void setFailed(boolean failed) {
      this.failed = failed;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
