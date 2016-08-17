package com.worldventures.dreamtrips.modules.infopages.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedbackType implements Parcelable {

   private int id;
   private String text;

   public FeedbackType() {
   }

   public FeedbackType(int id, String text) {
      this.id = id;
      this.text = text;
   }

   public int getId() {
      return id;
   }

   public String getText() {
      return text;
   }

   @Override
   public String toString() {
      return text;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
      dest.writeString(this.text);
   }

   protected FeedbackType(Parcel in) {
      this.id = in.readInt();
      this.text = in.readString();
   }

   public static final Creator<FeedbackType> CREATOR = new Creator<FeedbackType>() {
      public FeedbackType createFromParcel(Parcel source) {
         return new FeedbackType(source);
      }

      public FeedbackType[] newArray(int size) {
         return new FeedbackType[size];
      }
   };
}
