package com.worldventures.dreamtrips.modules.membership.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class UrlBundle implements Parcelable {

   private String url;

   public UrlBundle(String url) {
      this.url = url;
   }

   protected UrlBundle(Parcel in) {
      url = in.readString();
   }

   public static final Creator<UrlBundle> CREATOR = new Creator<UrlBundle>() {
      @Override
      public UrlBundle createFromParcel(Parcel in) {
         return new UrlBundle(in);
      }

      @Override
      public UrlBundle[] newArray(int size) {
         return new UrlBundle[size];
      }
   };

   public String getUrl() {
      return url;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {

      parcel.writeString(url);
   }

   @Override
   public int describeContents() {
      return 0;
   }
}
