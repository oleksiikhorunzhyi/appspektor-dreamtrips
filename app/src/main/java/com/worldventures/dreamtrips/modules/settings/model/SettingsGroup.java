package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingsGroup implements Parcelable {

   private String title;
   private Type type;

   public SettingsGroup() {

   }

   public SettingsGroup(Type type, String title) {
      this.type = type;
      this.title = title;
   }

   protected SettingsGroup(Parcel in) {
      title = in.readString();
      type = (Type) in.readSerializable();
   }

   public static final Creator<SettingsGroup> CREATOR = new Creator<SettingsGroup>() {
      @Override
      public SettingsGroup createFromParcel(Parcel in) {
         return new SettingsGroup(in);
      }

      @Override
      public SettingsGroup[] newArray(int size) {
         return new SettingsGroup[size];
      }
   };

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Type getType() {
      return type;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(title);
      dest.writeSerializable(type);
   }

   public enum Type {
      GENERAL, NOTIFICATIONS,
   }
}
