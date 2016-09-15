package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class PhotoBundle implements Parcelable {

   Photo photo;

   public PhotoBundle(Photo photo) {
      this.photo = photo;
   }

   public Photo getPhoto() {
      return photo;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.photo, 0);
   }

   protected PhotoBundle(Parcel in) {
      this.photo = in.readParcelable(Photo.class.getClassLoader());
   }

   public static final Creator<PhotoBundle> CREATOR = new Creator<PhotoBundle>() {
      public PhotoBundle createFromParcel(Parcel source) {
         return new PhotoBundle(source);
      }

      public PhotoBundle[] newArray(int size) {
         return new PhotoBundle[size];
      }
   };
}
