package com.worldventures.dreamtrips.modules.media_picker.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryBundle implements Parcelable {

   private boolean videoPickingEnabled;

   public GalleryBundle(boolean videoPickingEnabled) {
      this.videoPickingEnabled = videoPickingEnabled;
   }

   public boolean isVideoPickingEnabled() {
      return videoPickingEnabled;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {dest.writeByte(this.videoPickingEnabled ? (byte) 1 : (byte) 0);}

   protected GalleryBundle(Parcel in) {this.videoPickingEnabled = in.readByte() != 0;}

   public static final Creator<GalleryBundle> CREATOR = new Creator<GalleryBundle>() {
      @Override
      public GalleryBundle createFromParcel(Parcel source) {return new GalleryBundle(source);}

      @Override
      public GalleryBundle[] newArray(int size) {return new GalleryBundle[size];}
   };
}
