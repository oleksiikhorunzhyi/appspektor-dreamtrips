package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class FullScreenImagesBundle implements Parcelable {

   /**
    * Temp default tag state. Introduce users new feature
    * Due to Esmira Khasanogly requirement (15.01.2016)
    */
   private static final boolean SHOW_TAGS_BY_DEFAULT = true;

   public static final int NO_NOTIFICATION = -1;

   private TripImagesType type;
   private int userId;
   private int position;
   private ArrayList<IFullScreenObject> fixedList;
   private boolean foreign;
   private Route route;
   private int notificationId;
   private boolean showTags = SHOW_TAGS_BY_DEFAULT;

   public FullScreenImagesBundle() {
      notificationId = NO_NOTIFICATION;
   }

   protected FullScreenImagesBundle(Parcel in) {
      type = (TripImagesType) in.readSerializable();
      userId = in.readInt();
      position = in.readInt();
      fixedList = (ArrayList<IFullScreenObject>) in.readSerializable();
      foreign = in.readByte() == 1;
      route = (Route) in.readSerializable();
      notificationId = in.readInt();
      showTags = in.readByte() == 1;
   }

   public TripImagesType getType() {
      return type;
   }

   public int getUserId() {
      return userId;
   }

   public int getPosition() {
      return position;
   }

   public ArrayList<IFullScreenObject> getFixedList() {
      return fixedList;
   }

   public boolean isForeign() {
      return foreign;
   }

   public Route getRoute() {
      return route;
   }

   public int getNotificationId() {
      return notificationId;
   }

   public boolean isShowTags() {
      return showTags;
   }

   public static final Creator<FullScreenImagesBundle> CREATOR = new Creator<FullScreenImagesBundle>() {
      @Override
      public FullScreenImagesBundle createFromParcel(Parcel in) {
         return new FullScreenImagesBundle(in);
      }

      @Override
      public FullScreenImagesBundle[] newArray(int size) {
         return new FullScreenImagesBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeSerializable(type);
      parcel.writeInt(userId);
      parcel.writeInt(position);
      parcel.writeSerializable(fixedList);
      parcel.writeByte((byte) (foreign ? 1 : 0));
      parcel.writeSerializable(route);
      parcel.writeInt(notificationId);
      parcel.writeByte((byte) (showTags ? 1 : 0));
   }

   public static class Builder {

      private FullScreenImagesBundle instance;

      public Builder() {
         instance = new FullScreenImagesBundle();
      }

      public Builder type(TripImagesType type) {
         instance.type = type;
         return this;
      }

      public Builder userId(int userId) {
         instance.userId = userId;
         return this;
      }

      public Builder position(int position) {
         instance.position = position;
         return this;
      }

      public Builder fixedList(ArrayList<IFullScreenObject> list) {
         instance.fixedList = list;
         return this;
      }

      public Builder foreign(boolean foreign) {
         instance.foreign = foreign;
         return this;
      }

      public Builder route(Route route) {
         instance.route = route;
         return this;
      }

      public Builder notificationId(int notificationId) {
         instance.notificationId = notificationId;
         //
         if (notificationId == 0) instance.notificationId = NO_NOTIFICATION;
         return this;
      }

      public Builder showTags(boolean showTags) {
         instance.showTags = showTags;
         return this;
      }

      public FullScreenImagesBundle build() {
         return instance;
      }
   }
}
