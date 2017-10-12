package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter.SupportDeviceItemTypeFactory;

public class SupportedDeviceModel extends BaseViewModel<SupportDeviceItemTypeFactory> implements Parcelable {

   private String device;

   public SupportedDeviceModel() {}

   public SupportedDeviceModel(String device) {
      this.device = device;
   }

   protected SupportedDeviceModel(Parcel in) {
      this.device = in.readString();
   }


   public String getDevice() {
      return device;
   }

   public void setDevice(String device) {
      this.device = device;
   }

   @Override
   public int type(SupportDeviceItemTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.device);
   }

   public static final Creator<SupportedDeviceModel> CREATOR = new Creator<SupportedDeviceModel>() {
      @Override
      public SupportedDeviceModel createFromParcel(Parcel source) {
         return new SupportedDeviceModel(source);
      }

      @Override
      public SupportedDeviceModel[] newArray(int size) {
         return new SupportedDeviceModel[size];
      }
   };
}
