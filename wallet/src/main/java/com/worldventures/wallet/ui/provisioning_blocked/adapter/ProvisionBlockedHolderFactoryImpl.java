package com.worldventures.wallet.ui.provisioning_blocked.adapter;

import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.CustomerSupportContactHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.CustomerSupportContactModel;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDevicesListHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;
import com.worldventures.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class ProvisionBlockedHolderFactoryImpl implements ProvisionBlockedTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {

      if (viewType == R.layout.item_wallet_unsupported_device_info) {
         return new UnsupportedDeviceHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
      } else if (viewType == R.layout.item_wallet_unsupported_device_contact) {
         return new CustomerSupportContactHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
      } else if (viewType == R.layout.item_wallet_supported_devices_list) {
         return new SupportedDevicesListHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(UnsupportedDeviceModel model) {
      return R.layout.item_wallet_unsupported_device_info;
   }

   @Override
   public int type(CustomerSupportContactModel model) {
      return R.layout.item_wallet_unsupported_device_contact;
   }

   @Override
   public int type(SupportedDevicesListModel model) {
      return R.layout.item_wallet_supported_devices_list;
   }
}
