package com.worldventures.wallet.ui.provisioning_blocked.holder;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.provisioning_blocked.adapter.ProvisionBlockedTypeFactory;

import java.util.List;

public class SupportedDevicesListModel extends BaseViewModel<ProvisionBlockedTypeFactory> implements Parcelable {

   public final List<String> devices;

   public SupportedDevicesListModel(List<String> devices) {
      this.devices = devices;
   }

   @Override
   public int type(ProvisionBlockedTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeStringList(this.devices);
   }

   protected SupportedDevicesListModel(Parcel in) {
      this.devices = in.createStringArrayList();
   }

   public static final Creator<SupportedDevicesListModel> CREATOR = new Creator<SupportedDevicesListModel>() {
      @Override
      public SupportedDevicesListModel createFromParcel(Parcel source) {
         return new SupportedDevicesListModel(source);
      }

      @Override
      public SupportedDevicesListModel[] newArray(int size) {
         return new SupportedDevicesListModel[size];
      }
   };
}
