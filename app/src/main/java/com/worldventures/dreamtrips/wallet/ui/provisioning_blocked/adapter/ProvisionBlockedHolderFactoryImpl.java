package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.AdapterItemSupportedDevicesListBinding;
import com.worldventures.dreamtrips.databinding.AdapterItemUnsupportedDeviceInfoBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceModel;

public class ProvisionBlockedHolderFactoryImpl implements ProvisionBlockedTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {

      switch (viewType) {
         case R.layout.adapter_item_unsupported_device_info:
            AdapterItemUnsupportedDeviceInfoBinding unsupportedDeviceInfoBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new UnsupportedDeviceHolder(unsupportedDeviceInfoBinding);
         case R.layout.adapter_item_supported_devices_list:
            AdapterItemSupportedDevicesListBinding supportedDevicesListBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new SupportedDevicesListHolder(supportedDevicesListBinding);
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
      return R.layout.adapter_item_supported_devices_list;
   }
}
