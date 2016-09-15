package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;

public class YSBHPhoto extends Photo {


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
   }

   public YSBHPhoto() {
   }

   protected YSBHPhoto(Parcel in) {
      super(in);
   }

   public static final Creator<YSBHPhoto> CREATOR = new Creator<YSBHPhoto>() {
      public YSBHPhoto createFromParcel(Parcel source) {
         return new YSBHPhoto(source);
      }

      public YSBHPhoto[] newArray(int size) {
         return new YSBHPhoto[size];
      }
   };
}
