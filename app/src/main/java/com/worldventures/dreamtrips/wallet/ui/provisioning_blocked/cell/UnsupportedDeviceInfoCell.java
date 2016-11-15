package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell;


import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;


@Layout(R.layout.adapter_item_unsupported_device_info)
public class UnsupportedDeviceInfoCell extends AbstractDelegateCell<UnsupportedDeviceModel, CellDelegate<UnsupportedDeviceModel>> {

   public UnsupportedDeviceInfoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
   }
}
