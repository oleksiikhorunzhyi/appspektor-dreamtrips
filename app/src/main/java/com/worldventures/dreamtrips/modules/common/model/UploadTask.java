package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class UploadTask implements Parcelable, Serializable {

   protected static final long serialVersionUID = 1233322;

   private String filePath;
   private int progress;
   private Status status;
   private String key;
   private ArrayList<String> tags;

   private String title;

   private String locationName;
   private float latitude;
   private float longitude;
   private Date shotAt;

   private String originUrl;

   private String type;

   private long id;

   public UploadTask() {
      //do nothing
   }

   public String getFilePath() {
      return filePath;
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   public int getProgress() {
      return progress;
   }

   public void setProgress(int progress) {
      this.progress = progress;
   }

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public ArrayList<String> getTags() {
      return tags;
   }

   public void setTags(ArrayList<String> tags) {
      this.tags = tags;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
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

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setId(long id) {
      this.id = id;
   }

   public long getId() {
      return id;
   }

   public enum Status {
      COMPLETED, CANCELED, STARTED, FAILED
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      UploadTask that = (UploadTask) o;

      return id == that.id;

   }

   @Override
   public int hashCode() {
      return (int) (id ^ (id >>> 32));
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.filePath);
      dest.writeInt(this.progress);
      dest.writeInt(this.status == null ? -1 : this.status.ordinal());
      dest.writeString(this.key);
      dest.writeStringList(this.tags);
      dest.writeString(this.title);
      dest.writeString(this.locationName);
      dest.writeFloat(this.latitude);
      dest.writeFloat(this.longitude);
      dest.writeLong(shotAt != null ? shotAt.getTime() : -1);
      dest.writeString(this.originUrl);
      dest.writeString(this.type);
      dest.writeLong(this.id);
   }

   protected UploadTask(Parcel in) {
      this.filePath = in.readString();
      this.progress = in.readInt();
      int tmpStatus = in.readInt();
      this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
      this.key = in.readString();
      this.tags = in.createStringArrayList();
      this.title = in.readString();
      this.locationName = in.readString();
      this.latitude = in.readFloat();
      this.longitude = in.readFloat();
      long tmpShotAt = in.readLong();
      this.shotAt = tmpShotAt == -1 ? null : new Date(tmpShotAt);
      this.originUrl = in.readString();
      this.type = in.readString();
      this.id = in.readLong();
   }

   public static final Creator<UploadTask> CREATOR = new Creator<UploadTask>() {
      public UploadTask createFromParcel(Parcel source) {
         return new UploadTask(source);
      }

      public UploadTask[] newArray(int size) {
         return new UploadTask[size];
      }
   };
}
