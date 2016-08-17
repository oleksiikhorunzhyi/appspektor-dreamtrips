package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class Suggestion extends BaseEntity {

   private String name;

   public Suggestion() {
      super();
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
   }

   public Suggestion(Parcel in) {
      super(in);
      this.name = in.readString();
   }

   public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
      @Override
      public Suggestion createFromParcel(Parcel in) {
         return new Suggestion(in);
      }

      @Override
      public Suggestion[] newArray(int size) {
         return new Suggestion[size];
      }
   };
}
