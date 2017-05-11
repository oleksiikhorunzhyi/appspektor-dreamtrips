package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.os.Parcel;
import android.os.Parcelable;

public class DescriptionReviewBundle implements Parcelable {

   private String text;

   public DescriptionReviewBundle(String text) {
      this.text = text;
   }

   public String getText() {
      return text;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.text);
   }

   public DescriptionReviewBundle() {
   }

   protected DescriptionReviewBundle(Parcel in) {
      this.text = in.readString();
   }

   public static final Creator<DescriptionReviewBundle> CREATOR = new Creator<DescriptionReviewBundle>() {
      @Override
      public DescriptionReviewBundle createFromParcel(Parcel source) {
         return new DescriptionReviewBundle(source);
      }

      @Override
      public DescriptionReviewBundle[] newArray(int size) {
         return new DescriptionReviewBundle[size];
      }
   };
}
