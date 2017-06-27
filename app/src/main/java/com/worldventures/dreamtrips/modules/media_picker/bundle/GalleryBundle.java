package com.worldventures.dreamtrips.modules.media_picker.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryBundle implements Parcelable {

   private boolean videoPickingEnabled;
   private int videoMaxLength;

   public GalleryBundle(boolean videoPickingEnabled, int videoMaxLength) {
      this.videoPickingEnabled = videoPickingEnabled;
      this.videoMaxLength = videoMaxLength;
   }

   public boolean isVideoPickingEnabled() {
      return videoPickingEnabled;
   }

   public int getVideoMaxLength() {
      return videoMaxLength;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(this.videoPickingEnabled ? (byte) 1 : (byte) 0);
      dest.writeInt(videoMaxLength);
   }

   protected GalleryBundle(Parcel in) {
      this.videoPickingEnabled = in.readByte() != 0;
      this.videoMaxLength = in.readInt();
   }

   public static final Creator<GalleryBundle> CREATOR = new Creator<GalleryBundle>() {
      @Override
      public GalleryBundle createFromParcel(Parcel source) {return new GalleryBundle(source);}

      @Override
      public GalleryBundle[] newArray(int size) {return new GalleryBundle[size];}
   };
}
