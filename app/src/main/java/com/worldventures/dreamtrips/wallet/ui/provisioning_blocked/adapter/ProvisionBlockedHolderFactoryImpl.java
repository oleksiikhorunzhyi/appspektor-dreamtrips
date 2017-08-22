package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class ProvisionBlockedHolderFactoryImpl implements ProvisionBlockedTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {

      switch (viewType) {
         case R.layout.adapter_item_unsupported_device_info:
            return new UnsupportedDeviceHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         case R.layout.item_wallet_supported_devices_list:
            return new SupportedDevicesListHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(UnsupportedDeviceModel model) {
      return R.layout.adapter_item_unsupported_device_info;
   }

   @Override
   public int type(SupportedDevicesListModel model) {
      return R.layout.item_wallet_supported_devices_list;
   }
}
