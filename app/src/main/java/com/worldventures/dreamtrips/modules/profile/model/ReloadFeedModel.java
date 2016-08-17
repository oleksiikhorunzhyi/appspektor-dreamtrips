package com.worldventures.dreamtrips.modules.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReloadFeedModel implements Parcelable {
   boolean visible;

   public boolean isVisible() {
      return visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(visible ? (byte) 1 : (byte) 0);
   }

   public ReloadFeedModel() {
   }

   protected ReloadFeedModel(Parcel in) {
      this.visible = in.readByte() != 0;
   }

   public static final Creator<ReloadFeedModel> CREATOR = new Creator<ReloadFeedModel>() {
      public ReloadFeedModel createFromParcel(Parcel source) {
         return new ReloadFeedModel(source);
      }

      public ReloadFeedModel[] newArray(int size) {
         return new ReloadFeedModel[size];
      }
   };
}
