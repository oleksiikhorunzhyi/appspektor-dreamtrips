package com.worldventures.dreamtrips.modules.friends.bundle;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FriendMainBundle implements Parcelable {
   private int defaultPosition;

   public static final int SEARCH = 0;
   public static final int REQUESTS = 1;


   public FriendMainBundle() {
      this.defaultPosition = SEARCH;
   }

   public FriendMainBundle(@DefaultPosition int defaultPosition) {
      this.defaultPosition = defaultPosition;
   }

   public int getDefaultPosition() {
      return defaultPosition;
   }

   @IntDef({SEARCH, REQUESTS})
   @Retention(RetentionPolicy.SOURCE)
   public @interface DefaultPosition {}


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.defaultPosition);
   }

   protected FriendMainBundle(Parcel in) {
      this.defaultPosition = in.readInt();
   }

   public static final Creator<FriendMainBundle> CREATOR = new Creator<FriendMainBundle>() {
      public FriendMainBundle createFromParcel(Parcel source) {
         return new FriendMainBundle(source);
      }

      public FriendMainBundle[] newArray(int size) {
         return new FriendMainBundle[size];
      }
   };
}
