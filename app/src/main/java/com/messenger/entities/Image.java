package com.messenger.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Image implements Parcelable, Serializable {

   public static final Creator<Image> CREATOR = new Creator<Image>() {
      public Image createFromParcel(Parcel source) {
         return new Image(source);
      }

      public Image[] newArray(int size) {
         return new Image[size];
      }
   };

   private String url;

   public Image() {
      //do nothing
   }

   private Image(Parcel in) {
      this.url = in.readString();
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.url);
   }
}
