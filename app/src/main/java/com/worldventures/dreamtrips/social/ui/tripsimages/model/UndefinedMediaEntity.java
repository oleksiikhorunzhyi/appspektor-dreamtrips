package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import android.os.Parcel;

public class UndefinedMediaEntity extends BaseMediaEntity<Photo> {

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
   }

   public UndefinedMediaEntity() {}

   protected UndefinedMediaEntity(Parcel in) {
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : TripImageType.values()[tmpType];
   }

   public static final Creator<UndefinedMediaEntity> CREATOR = new Creator<UndefinedMediaEntity>() {
      @Override
      public UndefinedMediaEntity createFromParcel(Parcel source) {return new UndefinedMediaEntity(source);}

      @Override
      public UndefinedMediaEntity[] newArray(int size) {return new UndefinedMediaEntity[size];}
   };
}
