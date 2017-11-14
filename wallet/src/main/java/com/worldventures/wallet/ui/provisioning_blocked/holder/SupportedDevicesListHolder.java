package com.worldventures.wallet.ui.provisioning_blocked.holder;

import com.worldventures.wallet.databinding.ItemWalletSupportedDevicesListBinding;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.wallet.ui.provisioning_blocked.adapter.SupportDeviceItemHolderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class SupportedDevicesListHolder extends BaseHolder<SupportedDevicesListModel> {

   private final MultiHolderAdapter<SupportedDeviceModel> adapter;

   public SupportedDevicesListHolder(ItemWalletSupportedDevicesListBinding binding) {
      super(binding.getRoot());
      binding.deviceList.getLayoutManager().setAutoMeasureEnabled(true);
      binding.deviceList.setNestedScrollingEnabled(false);

      adapter = new SimpleMultiHolderAdapter<>(new ArrayList<SupportedDeviceModel>(), new SupportDeviceItemHolderFactoryImpl());
      binding.deviceList.setAdapter(adapter);
   }

   @Override
   public void setData(SupportedDevicesListModel data) {
      final List<SupportedDeviceModel> deviceModels = convertToDeviceModel(data.devices);
      adapter.clear();
      adapter.addItems(deviceModels);
   }

   private List<SupportedDeviceModel> convertToDeviceModel(List<String> devices) {
      final List<SupportedDeviceModel> result = new ArrayList<>();
      for (String deviceName : devices) {
         result.add(new SupportedDeviceModel(deviceName));
      }
      return result;
   }
}
