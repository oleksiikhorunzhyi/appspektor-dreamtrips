package com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag;

import android.os.Parcel;
import android.os.Parcelable;

public class HashtagSuggestion implements Parcelable {

   private String name;
   private int usageCount;

   public HashtagSuggestion(String name, int usageCount) {
      this.name = name;
      this.usageCount = usageCount;
   }

   public HashtagSuggestion() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getUsageCount() {
      return usageCount;
   }

   public void setUsageCount(int usageCount) {
      this.usageCount = usageCount;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeInt(usageCount);
   }

   protected HashtagSuggestion(Parcel in) {
      this.name = in.readString();
      this.usageCount = in.readInt();
   }

   public static final Creator<HashtagSuggestion> CREATOR = new Creator<HashtagSuggestion>() {
      @Override
      public HashtagSuggestion createFromParcel(Parcel source) {
         return new HashtagSuggestion(source);
      }

      @Override
      public HashtagSuggestion[] newArray(int size) {
         return new HashtagSuggestion[size];
      }
   };
}
