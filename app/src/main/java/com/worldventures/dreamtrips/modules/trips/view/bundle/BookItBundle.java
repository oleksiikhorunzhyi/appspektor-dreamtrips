package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class BookItBundle implements Parcelable {

   String tripId;

   public String getTripId() {
      return tripId;
   }

   public void setTripId(String tripId) {
      this.tripId = tripId;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.tripId);
   }

   public BookItBundle() {
   }

   protected BookItBundle(Parcel in) {
      this.tripId = in.readString();
   }

   public static final Creator<BookItBundle> CREATOR = new Creator<BookItBundle>() {
      public BookItBundle createFromParcel(Parcel source) {
         return new BookItBundle(source);
      }

      public BookItBundle[] newArray(int size) {
         return new BookItBundle[size];
      }
   };
}
