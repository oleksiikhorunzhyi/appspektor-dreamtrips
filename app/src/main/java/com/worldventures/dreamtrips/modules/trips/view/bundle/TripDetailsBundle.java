package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class TripDetailsBundle implements Parcelable {

   TripModel tripModel;

   public TripDetailsBundle(TripModel tripModel) {
      this.tripModel = tripModel;
   }

   public TripModel tripModel() {
      return tripModel;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.tripModel);
   }

   protected TripDetailsBundle(Parcel in) {
      this.tripModel = (TripModel) in.readSerializable();
   }

   public static final Creator<TripDetailsBundle> CREATOR = new Creator<TripDetailsBundle>() {
      public TripDetailsBundle createFromParcel(Parcel source) {
         return new TripDetailsBundle(source);
      }

      public TripDetailsBundle[] newArray(int size) {
         return new TripDetailsBundle[size];
      }
   };
}
