package com.worldventures.dreamtrips.social.ui.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedBundle implements Parcelable {

   protected FeedBundle(Parcel in) { //NOPMD
      //do nothing
   }

   public static final Creator<FeedBundle> CREATOR = new Creator<FeedBundle>() {
      @Override
      public FeedBundle createFromParcel(Parcel in) {
         return new FeedBundle(in);
      }

      @Override
      public FeedBundle[] newArray(int size) {
         return new FeedBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      //do nothing
   }
}
