package com.worldventures.dreamtrips.modules.settings.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

public class SelectSetting extends Setting<String> {

   private List<String> options;

   public List<String> getOptions() {
      return options;
   }

   public void setOptions(List<String> options) {
      this.options = options;
   }

   public SelectSetting() {

   }

   public SelectSetting(String name, Type type, String value, List<String> options) {
      super(name, type, value);
      this.options = options;
   }

   protected SelectSetting(Parcel in) {
      name = in.readString();
      type = (Type) in.readSerializable();
      value = (String) in.readSerializable();
      options = new ArrayList<>();
      in.readStringList(options);
   }

   public static final Creator<SelectSetting> CREATOR = new Creator<SelectSetting>() {
      @Override
      public SelectSetting createFromParcel(Parcel in) {
         return new SelectSetting(in);
      }

      @Override
      public SelectSetting[] newArray(int size) {
         return new SelectSetting[size];
      }
   };

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
      dest.writeSerializable(type);
      dest.writeSerializable(value);
      dest.writeStringList(options);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      SelectSetting that = (SelectSetting) o;

      return options != null && that.options != null && options.containsAll(that.options) && that.options.containsAll(options);

   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (options != null ? options.hashCode() : 0);
      return result;
   }
}
