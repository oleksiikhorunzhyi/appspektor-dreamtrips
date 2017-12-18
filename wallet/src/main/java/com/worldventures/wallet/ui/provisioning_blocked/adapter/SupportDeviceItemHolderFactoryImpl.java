package com.worldventures.wallet.ui.provisioning_blocked.adapter;

import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDeviceHolder;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDeviceModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class SupportDeviceItemHolderFactoryImpl implements SupportDeviceItemTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      if (viewType == R.layout.item_wallet_supported_device_item) {
         return new SupportedDeviceHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(SupportedDeviceModel device) {
      return R.layout.item_wallet_supported_device_item;
   }
}
