package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;

import com.worldventures.dreamtrips.databinding.ItemWalletSupportedDeviceItemBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;

public class UnsupportedDeviceHolder extends BaseHolder<SupportedDevicesListModel> {

   public UnsupportedDeviceHolder(ItemWalletSupportedDeviceItemBinding binding) {
      super(binding.getRoot());
   }

   @Override
   public void setData(SupportedDevicesListModel data) {}
}
