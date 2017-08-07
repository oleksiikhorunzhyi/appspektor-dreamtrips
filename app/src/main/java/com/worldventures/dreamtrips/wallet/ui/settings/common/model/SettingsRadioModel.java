package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.DefaultCardHolderTypeFactory;

public class SettingsRadioModel extends BaseViewModel<DefaultCardHolderTypeFactory> implements Parcelable {

   private final int textResId;
   private final long value;

   public SettingsRadioModel(int textResId, long value) {
      this.textResId = textResId;
      this.value = value;
   }

   public int getTextResId() {
      return textResId;
   }

   public long getValue() {
      return value;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.textResId);
      dest.writeLong(this.value);
   }

   protected SettingsRadioModel(Parcel in) {
      this.textResId = in.readInt();
      this.value = in.readLong();
   }

   public static final Creator<SettingsRadioModel> CREATOR = new Creator<SettingsRadioModel>() {
      @Override
      public SettingsRadioModel createFromParcel(Parcel source) {return new SettingsRadioModel(source);}

      @Override
      public SettingsRadioModel[] newArray(int size) {return new SettingsRadioModel[size];}
   };

   @Override
   public int type(DefaultCardHolderTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
