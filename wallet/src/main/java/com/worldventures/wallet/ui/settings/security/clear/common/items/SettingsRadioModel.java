package com.worldventures.wallet.ui.settings.security.clear.common.items;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingsRadioModel implements Parcelable {

   private final String text;
   private final long value;

   SettingsRadioModel(String text, long value) {
      this.text = text;
      this.value = value;
   }

   public String getText() {
      return text;
   }

   public long getValue() {
      return value;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.text);
      dest.writeLong(this.value);
   }

   private SettingsRadioModel(Parcel in) {
      this.text = in.readString();
      this.value = in.readLong();
   }

   public static final Creator<SettingsRadioModel> CREATOR = new Creator<SettingsRadioModel>() {
      @Override
      public SettingsRadioModel createFromParcel(Parcel source) {return new SettingsRadioModel(source);}

      @Override
      public SettingsRadioModel[] newArray(int size) {return new SettingsRadioModel[size];}
   };
}
