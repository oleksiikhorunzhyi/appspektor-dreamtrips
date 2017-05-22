package com.worldventures.dreamtrips.modules.infopages.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Document implements Parcelable {

   private String name;
   private String originalName;
   private String url;

   public Document() {
   }

   public Document(String name, String originalName, String url) {
      this.name = name;
      this.originalName = originalName;
      this.url = url;
   }

   public String getName() {
      return name;
   }

   public String getOriginalName() {
      return originalName;
   }

   public String getUrl() {
      return url;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeString(this.originalName);
      dest.writeString(this.url);
   }

   protected Document(Parcel in) {
      this.name = in.readString();
      this.originalName = in.readString();
      this.url = in.readString();
   }

   public static final Creator<Document> CREATOR = new Creator<Document>() {
      @Override
      public Document createFromParcel(Parcel source) {return new Document(source);}

      @Override
      public Document[] newArray(int size) {return new Document[size];}
   };
}
