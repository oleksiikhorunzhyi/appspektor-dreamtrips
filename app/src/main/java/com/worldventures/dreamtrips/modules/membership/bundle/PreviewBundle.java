package com.worldventures.dreamtrips.modules.membership.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class PreviewBundle implements Parcelable {

   private String url;

   public PreviewBundle(String url) {
      this.url = url;
   }

   protected PreviewBundle(Parcel in) {
      url = in.readString();
   }

   public static final Creator<PreviewBundle> CREATOR = new Creator<PreviewBundle>() {
      @Override
      public PreviewBundle createFromParcel(Parcel in) {
         return new PreviewBundle(in);
      }

      @Override
      public PreviewBundle[] newArray(int size) {
         return new PreviewBundle[size];
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
