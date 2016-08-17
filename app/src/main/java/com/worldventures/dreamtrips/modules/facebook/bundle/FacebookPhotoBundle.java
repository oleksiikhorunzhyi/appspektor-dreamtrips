package com.worldventures.dreamtrips.modules.facebook.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class FacebookPhotoBundle implements Parcelable {
   String albumId;

   public FacebookPhotoBundle(String albumId) {
      this.albumId = albumId;
   }

   public String getAlbumId() {
      return albumId;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.albumId);
   }

   protected FacebookPhotoBundle(Parcel in) {
      this.albumId = in.readString();
   }

   public static final Creator<FacebookPhotoBundle> CREATOR = new Creator<FacebookPhotoBundle>() {
      public FacebookPhotoBundle createFromParcel(Parcel source) {
         return new FacebookPhotoBundle(source);
      }

      public FacebookPhotoBundle[] newArray(int size) {
         return new FacebookPhotoBundle[size];
      }
   };
}
