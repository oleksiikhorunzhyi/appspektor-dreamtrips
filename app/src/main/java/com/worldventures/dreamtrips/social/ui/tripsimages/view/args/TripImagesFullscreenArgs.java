package com.worldventures.dreamtrips.social.ui.tripsimages.view.args;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;

import java.util.List;

public class TripImagesFullscreenArgs implements Parcelable {
   private TripImagesArgs tripImagesArgs;
   private List<BaseMediaEntity> mediaEntityList;
   private boolean lastPageReached;
   private int currentItemPosition;
   private int notificationId;

   private TripImagesFullscreenArgs() {
   }

   public TripImagesArgs getTripImagesArgs() {
      return tripImagesArgs;
   }

   public List<BaseMediaEntity> getMediaEntityList() {
      return mediaEntityList;
   }

   public int getCurrentItem() {
      return currentItemPosition;
   }

   public boolean isLastPageReached() {
      return lastPageReached;
   }

   public int getNotificationId() {
      return notificationId;
   }

   protected TripImagesFullscreenArgs(Parcel in) {
      tripImagesArgs = in.readParcelable(TripImagesArgs.class.getClassLoader());
      mediaEntityList = in.readArrayList(BaseMediaEntity.class.getClassLoader());
      lastPageReached = in.readInt() == 1;
      currentItemPosition = in.readInt();
      notificationId = in.readInt();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(tripImagesArgs, 0);
      dest.writeList(mediaEntityList);
      dest.writeInt(lastPageReached ? 1 : 0);
      dest.writeInt(currentItemPosition);
      dest.writeInt(notificationId);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<TripImagesFullscreenArgs> CREATOR = new Creator<TripImagesFullscreenArgs>() {
      @Override
      public TripImagesFullscreenArgs createFromParcel(Parcel in) {
         return new TripImagesFullscreenArgs(in);
      }

      @Override
      public TripImagesFullscreenArgs[] newArray(int size) {
         return new TripImagesFullscreenArgs[size];
      }
   };

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private TripImagesArgs tripImagesArgs;
      private List<BaseMediaEntity> mediaEntityList;
      private boolean lastPageReached;
      private int currentItemPosition;
      private int notificationId;

      public Builder tripImagesArgs(TripImagesArgs tripImagesArgs) {
         this.tripImagesArgs = tripImagesArgs;
         return this;
      }

      public Builder mediaEntityList(List<BaseMediaEntity> mediaEntityList) {
         this.mediaEntityList = mediaEntityList;
         return this;

      }

      public Builder lastPageReached(boolean lastPageReached) {
         this.lastPageReached = lastPageReached;
         return this;

      }

      public Builder currentItemPosition(int currentItemPosition) {
         this.currentItemPosition = currentItemPosition;
         return this;
      }

      public Builder notificationId(int notificationId) {
         this.notificationId = notificationId;
         return this;

      }

      public TripImagesFullscreenArgs build() {
         TripImagesFullscreenArgs tripImagesFullscreenArgs = new TripImagesFullscreenArgs();
         tripImagesFullscreenArgs.tripImagesArgs = tripImagesArgs;
         tripImagesFullscreenArgs.mediaEntityList = mediaEntityList;
         tripImagesFullscreenArgs.lastPageReached = lastPageReached;
         tripImagesFullscreenArgs.currentItemPosition = currentItemPosition;
         tripImagesFullscreenArgs.notificationId = notificationId;
         return tripImagesFullscreenArgs;
      }
   }
}
