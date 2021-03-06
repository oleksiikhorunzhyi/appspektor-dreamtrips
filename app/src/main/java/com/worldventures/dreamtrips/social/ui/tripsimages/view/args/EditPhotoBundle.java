package com.worldventures.dreamtrips.social.ui.tripsimages.view.args;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

public class EditPhotoBundle implements Parcelable {

   private final Photo photo;

   public EditPhotoBundle(Photo photo) {
      this.photo = photo;
   }

   protected EditPhotoBundle(Parcel in) {
      photo = in.readParcelable(Photo.class.getClassLoader());
   }

   public Photo getPhoto() {
      return photo;
   }

   public static final Creator<EditPhotoBundle> CREATOR = new Creator<EditPhotoBundle>() {
      @Override
      public EditPhotoBundle createFromParcel(Parcel in) {
         return new EditPhotoBundle(in);
      }

      @Override
      public EditPhotoBundle[] newArray(int size) {
         return new EditPhotoBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(photo, flags);
   }
}
