package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder;

import com.worldventures.dreamtrips.databinding.AdapterItemSupportedDevicesListBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter.SupportDeviceItemHolderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class SupportedDevicesListHolder extends BaseHolder<SupportedDevicesListModel> {

   private MultiHolderAdapter<SupportedDeviceModel> adapter;

   public SupportedDevicesListHolder(AdapterItemSupportedDevicesListBinding binding) {
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
