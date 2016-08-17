package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.Coordinates;

public class Cluster extends MapObject {

   private int tripCount;
   private ZoomCoordinates zoomCoordinates;

   public int getTripCount() {
      return tripCount;
   }

   public Coordinates getTopLeft() {
      return zoomCoordinates.getTopLeft();
   }

   public Coordinates getBottomRight() {
      return zoomCoordinates.getBottomRight();
   }

   public static class ZoomCoordinates implements Parcelable {

      private Coordinates topLeft;
      private Coordinates bottomRight;

      public Coordinates getTopLeft() {
         return topLeft;
      }

      public Coordinates getBottomRight() {
         return bottomRight;
      }


      @Override
      public int describeContents() {
         return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeParcelable(this.topLeft, flags);
         dest.writeParcelable(this.bottomRight, flags);
      }

      public ZoomCoordinates() {
      }

      protected ZoomCoordinates(Parcel in) {
         this.topLeft = in.readParcelable(Coordinates.class.getClassLoader());
         this.bottomRight = in.readParcelable(Coordinates.class.getClassLoader());
      }

      public static final Creator<ZoomCoordinates> CREATOR = new Creator<ZoomCoordinates>() {
         @Override
         public ZoomCoordinates createFromParcel(Parcel source) {
            return new ZoomCoordinates(source);
         }

         @Override
         public ZoomCoordinates[] newArray(int size) {
            return new ZoomCoordinates[size];
         }
      };
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(this.tripCount);
      dest.writeParcelable(this.zoomCoordinates, flags);
      dest.writeParcelable(this.coordinates, flags);
   }

   public Cluster() {
   }

   protected Cluster(Parcel in) {
      super(in);
      this.tripCount = in.readInt();
      this.zoomCoordinates = in.readParcelable(ZoomCoordinates.class.getClassLoader());
      this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
   }

   public static final Creator<Cluster> CREATOR = new Creator<Cluster>() {
      @Override
      public Cluster createFromParcel(Parcel source) {
         return new Cluster(source);
      }

      @Override
      public Cluster[] newArray(int size) {
         return new Cluster[size];
      }
   };
}
