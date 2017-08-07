package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;

import com.worldventures.dreamtrips.databinding.AdapterItemSupportedDeviceItemBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;

public class SupportedDeviceHolder extends BaseHolder<SupportedDeviceModel> {

   private final AdapterItemSupportedDeviceItemBinding binding;

   public SupportedDeviceHolder(AdapterItemSupportedDeviceItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(SupportedDeviceModel data) {
      binding.text1.setText(data.getDevice());
   }
}
