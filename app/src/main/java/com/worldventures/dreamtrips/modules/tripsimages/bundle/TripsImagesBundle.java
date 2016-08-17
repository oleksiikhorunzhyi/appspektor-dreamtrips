package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public class TripsImagesBundle implements Parcelable {

   private TripImagesType type;
   private int userId;

   public TripsImagesBundle(TripImagesType type, int userId) {
      this.type = type;
      this.userId = userId;
   }

   protected TripsImagesBundle(Parcel in) {
      userId = in.readInt();
      type = (TripImagesType) in.readSerializable();
   }

   public static final Creator<TripsImagesBundle> CREATOR = new Creator<TripsImagesBundle>() {
      @Override
      public TripsImagesBundle createFromParcel(Parcel in) {
         return new TripsImagesBundle(in);
      }

      @Override
      public TripsImagesBundle[] newArray(int size) {
         return new TripsImagesBundle[size];
      }
   };

   public TripImagesType getType() {
      return type;
   }

   public int getUserId() {
      return userId;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeInt(userId);
      parcel.writeSerializable(type);
   }
}
