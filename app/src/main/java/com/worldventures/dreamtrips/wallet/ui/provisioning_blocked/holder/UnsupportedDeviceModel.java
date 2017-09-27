package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter.ProvisionBlockedTypeFactory;

public class UnsupportedDeviceModel extends BaseViewModel<ProvisionBlockedTypeFactory> implements Parcelable {

   @Override
   public int type(ProvisionBlockedTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {}

   public UnsupportedDeviceModel() {}

   protected UnsupportedDeviceModel(Parcel in) {}

   public static final Creator<UnsupportedDeviceModel> CREATOR = new Creator<UnsupportedDeviceModel>() {
      @Override
      public UnsupportedDeviceModel createFromParcel(Parcel source) {return new UnsupportedDeviceModel(source);}

      @Override
      public UnsupportedDeviceModel[] newArray(int size) {return new UnsupportedDeviceModel[size];}
   };
}
