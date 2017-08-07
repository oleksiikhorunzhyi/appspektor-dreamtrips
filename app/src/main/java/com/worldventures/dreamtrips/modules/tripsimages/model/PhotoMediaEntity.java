package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

import java.util.Date;

public class PhotoMediaEntity extends BaseMediaEntity {

   private String url;
   public Photo photo;

   public Photo getPhoto() {
      return photo;
   }

   public void setPhoto(Photo photo) {
      this.photo = photo;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
      dest.writeParcelable(this.photo, flags);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeString(this.uid);
      dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
   }

   public PhotoMediaEntity() {}

   protected PhotoMediaEntity(Parcel in) {
      this.url = in.readString();
      this.photo = in.readParcelable(Photo.class.getClassLoader());
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : TripImageType.values()[tmpType];
      this.uid = in.readString();
      long tmpCreatedAt = in.readLong();
      this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
   }

   public static final Creator<PhotoMediaEntity> CREATOR = new Creator<PhotoMediaEntity>() {
      @Override
      public PhotoMediaEntity createFromParcel(Parcel source) {return new PhotoMediaEntity(source);}

      @Override
      public PhotoMediaEntity[] newArray(int size) {return new PhotoMediaEntity[size];}
   };
}
