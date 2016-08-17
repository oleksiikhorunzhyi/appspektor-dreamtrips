package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Flag implements Parcelable {
   private int id;

   private String name;

   @SerializedName("require_description") private boolean requireDescription;

   Flag() {
   }

   public Flag(int id, String name, boolean requireDescription) {
      this.id = id;
      this.name = name;
      this.requireDescription = requireDescription;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public boolean isRequireDescription() {
      return requireDescription;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
      dest.writeString(this.name);
      dest.writeByte(requireDescription ? (byte) 1 : (byte) 0);
   }

   protected Flag(Parcel in) {
      this.id = in.readInt();
      this.name = in.readString();
      this.requireDescription = in.readByte() != 0;
   }

   public static final Creator<Flag> CREATOR = new Creator<Flag>() {
      @Override
      public Flag createFromParcel(Parcel source) {
         return new Flag(source);
      }

      @Override
      public Flag[] newArray(int size) {
         return new Flag[size];
      }
   };
}
