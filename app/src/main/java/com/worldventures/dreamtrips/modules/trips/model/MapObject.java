package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.Coordinates;

import java.io.Serializable;

public class MapObject implements Parcelable, Serializable {

   protected Coordinates coordinates;

   public Coordinates getCoordinates() {
      return coordinates;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.coordinates, flags);
   }

   public MapObject() {
   }

   protected MapObject(Parcel in) {
      this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
   }

}
