package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Inspiration implements Parcelable {

   private String id;
   private String url;
   private String quote;
   private String author;

   public Inspiration() {
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getQuote() {
      return String.format("\"%s\"", quote);
   }

   public void setQuote(String quote) {
      this.quote = quote;
   }

   public String getAuthor() {
      return author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.id);
      dest.writeString(this.url);
      dest.writeString(this.quote);
      dest.writeString(this.author);
   }

   protected Inspiration(Parcel in) {
      this.id = in.readString();
      this.url = in.readString();
      this.quote = in.readString();
      this.author = in.readString();
   }

   public static final Creator<Inspiration> CREATOR = new Creator<Inspiration>() {
      @Override
      public Inspiration createFromParcel(Parcel source) {
         return new Inspiration(source);
      }

      @Override
      public Inspiration[] newArray(int size) {
         return new Inspiration[size];
      }
   };
}
