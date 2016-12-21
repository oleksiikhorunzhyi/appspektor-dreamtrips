package com.worldventures.dreamtrips.modules.infopages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentBundle implements Parcelable {

   private String url;
   private String title;

   public DocumentBundle(String url, String title) {
      this.url = url;
      this.title = title;
   }

   public String getUrl() {
      return url;
   }

   public String getTitle() {
      return title;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
      dest.writeString(this.title);
   }

   protected DocumentBundle(Parcel in) {
      this.url = in.readString();
      this.title = in.readString();
   }

   public static final Creator<DocumentBundle> CREATOR = new Creator<DocumentBundle>() {
      @Override
      public DocumentBundle createFromParcel(Parcel source) {return new DocumentBundle(source);}

      @Override
      public DocumentBundle[] newArray(int size) {return new DocumentBundle[size];}
   };
}
