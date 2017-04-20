package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;

public class CreateEntityBundle implements Parcelable {

   private MediaAttachment mediaAttachment;
   private boolean showPickerImmediately;
   private Origin origin; // origin is needed for analytics

   public CreateEntityBundle(boolean showPickerImmediately, Origin origin) {
      this.showPickerImmediately = showPickerImmediately;
      this.origin = origin;
   }

   public CreateEntityBundle(MediaAttachment mediaAttachment, Origin origin) {
      this.mediaAttachment = mediaAttachment;
      this.origin = origin;
   }

   public MediaAttachment getMediaAttachment() {
      return mediaAttachment;
   }

   public Origin getOrigin() {
      return origin;
   }

   public boolean isShowPickerImmediately() {
      return showPickerImmediately;
   }

   public void setShowPickerImmediately(boolean showPickerImmediately) {
      this.showPickerImmediately = showPickerImmediately;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.mediaAttachment, flags);
      dest.writeByte(showPickerImmediately ? (byte) 1 : (byte) 0);
   }

   protected CreateEntityBundle(Parcel in) {
      this.mediaAttachment = in.readParcelable(MediaAttachment.class.getClassLoader());
      this.showPickerImmediately = in.readByte() != 0;
   }

   public static final Creator<CreateEntityBundle> CREATOR = new Creator<CreateEntityBundle>() {
      @Override
      public CreateEntityBundle createFromParcel(Parcel source) {
         return new CreateEntityBundle(source);
      }

      @Override
      public CreateEntityBundle[] newArray(int size) {
         return new CreateEntityBundle[size];
      }
   };

   public enum Origin {
      FEED, PROFILE_TRIP_IMAGES, MY_TRIP_IMAGES, MEMBER_TRIP_IMAGES
   }
}
