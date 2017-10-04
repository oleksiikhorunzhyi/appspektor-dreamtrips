package com.worldventures.dreamtrips.modules.trips.view.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage;

import java.util.List;

public class TripViewPagerBundle implements Parcelable {
   private List<TripImage> tripImages;
   private int position;

   public TripViewPagerBundle(List<TripImage> tripImages, int position) {
      this.tripImages = tripImages;
      this.position = position;
   }

   public List<TripImage> getTripImages() {
      return tripImages;
   }

   public int getPosition() {
      return position;
   }

   protected TripViewPagerBundle(Parcel in) {
      tripImages = in.readArrayList(TripImage.class.getClassLoader());
      position = in.readInt();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(tripImages);
      dest.writeInt(position);
   }

   public static final Creator<TripViewPagerBundle> CREATOR = new Creator<TripViewPagerBundle>() {
      @Override
      public TripViewPagerBundle createFromParcel(Parcel in) {
         return new TripViewPagerBundle(in);
      }

      @Override
      public TripViewPagerBundle[] newArray(int size) {
         return new TripViewPagerBundle[size];
      }
   };
}
