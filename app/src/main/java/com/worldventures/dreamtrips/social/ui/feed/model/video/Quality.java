package com.worldventures.dreamtrips.social.ui.feed.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Quality implements Parcelable, Serializable {

   private final String name;
   private final String url;

   public Quality(String name, String url) {
      this.name = name;
      this.url = url;
   }

   protected Quality(Parcel in) {
      name = in.readString();
      url = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
      dest.writeString(url);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<Quality> CREATOR = new Creator<Quality>() {
      @Override
      public Quality createFromParcel(Parcel in) {
         return new Quality(in);
      }

      @Override
      public Quality[] newArray(int size) {
         return new Quality[size];
      }
   };

   public String getUrl() {
      return url;
   }

   public String getName() {
      return name;
   }

}
