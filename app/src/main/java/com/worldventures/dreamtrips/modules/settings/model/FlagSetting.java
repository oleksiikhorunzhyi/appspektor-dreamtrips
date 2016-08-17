package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;

public class FlagSetting extends Setting<Boolean> {

   public FlagSetting() {

   }

   protected FlagSetting(Parcel in) {
      name = in.readString();
      type = (Type) in.readSerializable();
      value = (Boolean) in.readSerializable();
   }

   public FlagSetting(String name, Type type, boolean value) {
      super(name, type, value);
   }

   @Override
   public Boolean getValue() {
      return value == null ? false : value;
   }

   public static final Creator<FlagSetting> CREATOR = new Creator<FlagSetting>() {
      @Override
      public FlagSetting createFromParcel(Parcel in) {
         return new FlagSetting(in);
      }

      @Override
      public FlagSetting[] newArray(int size) {
         return new FlagSetting[size];
      }
   };

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
      dest.writeSerializable(type);
      dest.writeSerializable(value);
   }
}
