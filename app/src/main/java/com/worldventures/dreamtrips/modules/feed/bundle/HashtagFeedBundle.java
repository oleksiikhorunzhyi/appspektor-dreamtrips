package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class HashtagFeedBundle implements Parcelable {

   String hashtag;

   public HashtagFeedBundle(String hashtag) {
      this.hashtag = hashtag;
   }

   public String getHashtag() {
      return hashtag;
   }

   protected HashtagFeedBundle(Parcel in) {
      hashtag = in.readString();
   }

   public static final Creator<HashtagFeedBundle> CREATOR = new Creator<HashtagFeedBundle>() {
      @Override
      public HashtagFeedBundle createFromParcel(Parcel in) {
         return new HashtagFeedBundle(in);
      }

      @Override
      public HashtagFeedBundle[] newArray(int size) {
         return new HashtagFeedBundle[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(hashtag);
   }
}
