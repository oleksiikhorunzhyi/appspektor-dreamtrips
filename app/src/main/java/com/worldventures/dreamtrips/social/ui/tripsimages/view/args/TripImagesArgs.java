package com.worldventures.dreamtrips.social.ui.tripsimages.view.args;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;

public class TripImagesArgs implements Parcelable {
   private final int userId;
   private final boolean showTimestamps;
   private final int pageSize;
   private final TripImageType tripImageType;
   private final CreateEntityBundle.Origin origin;

   private TripImagesArgs(int userId, boolean showTimestamps, int pageSize, TripImageType tripImageType, CreateEntityBundle.Origin origin) {
      this.userId = userId;
      this.showTimestamps = showTimestamps;
      this.pageSize = pageSize;
      this.tripImageType = tripImageType;
      this.origin = origin;
   }

   public int getPageSize() {
      return pageSize;
   }

   public CreateEntityBundle.Origin getOrigin() {
      return origin;
   }

   public boolean showTimestamps() {
      return showTimestamps;
   }

   public int getUserId() {
      return userId;
   }

   public TripImageType getTripImageType() {
      return tripImageType;
   }

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      TripImagesArgs that = (TripImagesArgs) o;

      if (userId != that.userId) {
         return false;
      }
      if (showTimestamps != that.showTimestamps) {
         return false;
      }
      if (pageSize != that.pageSize) {
         return false;
      }
      if (tripImageType != that.tripImageType) {
         return false;
      }
      return origin == that.origin;

   }

   @Override
   public int hashCode() {
      int result = userId;
      result = 31 * result + (showTimestamps ? 1 : 0);
      result = 31 * result + pageSize;
      result = 31 * result + (tripImageType != null ? tripImageType.hashCode() : 0);
      result = 31 * result + (origin != null ? origin.hashCode() : 0);
      return result;
   }

   public static class Builder {
      private static final int DEFAULT_PAGE_SIZE = 40;

      private int userId;
      private boolean showTimestamps;
      private int pageSize = DEFAULT_PAGE_SIZE;
      private TripImageType tripImageType;
      private CreateEntityBundle.Origin origin;

      public Builder userId(int userId) {
         this.userId = userId;
         return this;
      }

      public Builder showTimestamps(boolean showTimestamps) {
         this.showTimestamps = showTimestamps;
         return this;
      }

      public Builder pageSize(int pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      public Builder type(TripImageType tripImageType) {
         this.tripImageType = tripImageType;
         return this;
      }

      public Builder origin(CreateEntityBundle.Origin origin) {
         this.origin = origin;
         return this;
      }

      public TripImagesArgs build() {
         return new TripImagesArgs(userId, showTimestamps, pageSize, tripImageType, origin);
      }
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.userId);
      dest.writeByte(this.showTimestamps ? (byte) 1 : (byte) 0);
      dest.writeInt(this.pageSize);
      dest.writeInt(this.tripImageType == null ? -1 : this.tripImageType.ordinal());
      dest.writeInt(this.origin == null ? -1 : this.origin.ordinal());
   }

   protected TripImagesArgs(Parcel in) {
      this.userId = in.readInt();
      this.showTimestamps = in.readByte() != 0;
      this.pageSize = in.readInt();
      int tmpRoute = in.readInt();
      this.tripImageType = tmpRoute == -1 ? null : TripImageType.values()[tmpRoute];
      int tmpOrigin = in.readInt();
      this.origin = tmpOrigin == -1 ? null : CreateEntityBundle.Origin.values()[tmpOrigin];
   }

   public static final Creator<TripImagesArgs> CREATOR = new Creator<TripImagesArgs>() {
      @Override
      public TripImagesArgs createFromParcel(Parcel source) {
         return new TripImagesArgs(source);
      }

      @Override
      public TripImagesArgs[] newArray(int size) {
         return new TripImagesArgs[size];
      }
   };

   public enum TripImageType {
      MEMBER_IMAGES, ACCOUNT_IMAGES
   }
}
