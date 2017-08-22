package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDeviceHolder;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDeviceModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class SupportDeviceItemHolderFactoryImpl implements SupportDeviceItemTypeFactory {

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.item_wallet_supported_device_item:
            return new SupportedDeviceHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(SupportedDeviceModel device) {
      return R.layout.item_wallet_supported_device_item;
   }
}
