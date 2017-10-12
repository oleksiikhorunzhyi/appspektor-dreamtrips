package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

public class YSBHPhoto implements Parcelable {
   private int id;
   private String url;
   private String title;

   public YSBHPhoto() {
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
      dest.writeString(this.url);
      dest.writeString(this.title);
   }

   protected YSBHPhoto(Parcel in) {
      this.id = in.readInt();
      this.url = in.readString();
      this.title = in.readString();
   }

   public static final Creator<YSBHPhoto> CREATOR = new Creator<YSBHPhoto>() {
      @Override
      public YSBHPhoto createFromParcel(Parcel source) {
         return new YSBHPhoto(source);
      }

      @Override
      public YSBHPhoto[] newArray(int size) {
         return new YSBHPhoto[size];
      }
   };
}
