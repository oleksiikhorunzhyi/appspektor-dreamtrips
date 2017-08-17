package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

public class PhotoMediaEntity extends BaseMediaEntity<Photo> {

   public PhotoMediaEntity(Photo photo) {
      type = TripImageType.PHOTO;
      item = photo;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeParcelable(this.item, 0);
   }

   public PhotoMediaEntity() {
      type = TripImageType.PHOTO;
   }

   protected PhotoMediaEntity(Parcel in) {
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : TripImageType.values()[tmpType];
      this.item = in.readParcelable(Photo.class.getClassLoader());
   }

   public static final Creator<PhotoMediaEntity> CREATOR = new Creator<PhotoMediaEntity>() {
      @Override
      public PhotoMediaEntity createFromParcel(Parcel source) {return new PhotoMediaEntity(source);}

      @Override
      public PhotoMediaEntity[] newArray(int size) {return new PhotoMediaEntity[size];}
   };
}
