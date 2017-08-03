package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.AdapterItemSupportedDeviceItemBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDeviceHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDeviceModel;

public class SupportDeviceItemHolderFactoryImpl implements SupportDeviceItemTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.adapter_item_supported_device_item:
            AdapterItemSupportedDeviceItemBinding supportedDeviceItemBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new SupportedDeviceHolder(supportedDeviceItemBinding);
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(SupportedDeviceModel device) {
      return R.layout.adapter_item_supported_device_item;
   }
}
