package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell;


import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;


@Layout(R.layout.adapter_item_supported_device_item)
public class SupportedDeviceItemCell extends AbstractDelegateCell<String, CellDelegate<String>> {

   @InjectView(android.R.id.text1) TextView textView;

   public SupportedDeviceItemCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textView.setText(getModelObject());
   }
}
