package com.worldventures.wallet.ui.provisioning_blocked.holder;

import com.worldventures.wallet.databinding.ItemWalletSupportedDeviceItemBinding;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;

public class SupportedDeviceHolder extends BaseHolder<SupportedDeviceModel> {

   private final ItemWalletSupportedDeviceItemBinding binding;

   public SupportedDeviceHolder(ItemWalletSupportedDeviceItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(SupportedDeviceModel data) {
      binding.setDeviceName(data.getDevice());
   }
}
