package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

public class PinHolder extends MapObjectHolder<Pin> {

   public PinHolder() {
   }

   protected PinHolder(Parcel in) {
      super(in);
   }

   public static final Creator<PinHolder> CREATOR = new Creator<PinHolder>() {
      @Override
      public PinHolder createFromParcel(Parcel source) {
         return new PinHolder(source);
      }

      @Override
      public PinHolder[] newArray(int size) {
         return new PinHolder[size];
      }
   };
}
