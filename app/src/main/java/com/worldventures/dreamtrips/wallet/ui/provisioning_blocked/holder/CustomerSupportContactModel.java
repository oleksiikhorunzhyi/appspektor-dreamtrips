package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter.ProvisionBlockedTypeFactory;

public class CustomerSupportContactModel extends BaseViewModel<ProvisionBlockedTypeFactory> implements Parcelable {

   @Override
   public int type(ProvisionBlockedTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {}

   public CustomerSupportContactModel() {}

   protected CustomerSupportContactModel(Parcel in) {}

   public static final Creator<CustomerSupportContactModel> CREATOR = new Creator<CustomerSupportContactModel>() {
      @Override
      public CustomerSupportContactModel createFromParcel(Parcel source) {
         return new CustomerSupportContactModel(source);
      }

      @Override
      public CustomerSupportContactModel[] newArray(int size) {
         return new CustomerSupportContactModel[size];
      }
   };
}
