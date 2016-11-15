package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_supported_devices_list)
public class SupportedDevicesListCell extends AbstractDelegateCell<SupportedDevicesListModel, CellDelegate<SupportedDevicesListModel>> {

   @Inject @ForActivity Injector injector;
   @InjectView(R.id.deviceList) RecyclerView recyclerView;
   BaseDelegateAdapter adapter;

   public SupportedDevicesListCell(View view) {
      super(view);

      LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
      layoutManager.setAutoMeasureEnabled(true);
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setNestedScrollingEnabled(false);
   }

   @Override
   public void afterInject() {
      adapter = new BaseDelegateAdapter(itemView.getContext(), injector);
      adapter.registerCell(String.class, SupportedDeviceItemCell.class);
      recyclerView.setAdapter(adapter);
   }

   @Override
   protected void syncUIStateWithModel() {
      adapter.clearAndUpdateItems(getModelObject().devices);
   }

}
