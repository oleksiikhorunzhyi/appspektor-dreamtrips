package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionHeaderModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_region_header)
public class HeaderRegionCell extends BaseAbstractDelegateCell<RegionHeaderModel, HeaderRegionCell.Delegate> {

   @InjectView(R.id.checkBoxSelectAllRegion) CheckBox checkBoxSelectAll;

   public HeaderRegionCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      checkBoxSelectAll.setChecked(getModelObject().isChecked());
   }

   @OnClick(R.id.checkBoxSelectAllRegion)
   void checkBoxClicked() {
      cellDelegate.onCheckBoxAllRegionsPressedEvent(checkBoxSelectAll.isChecked());

   }

   @OnClick(R.id.listHeader)
   void toggleVisibility() {
      cellDelegate.toggleVisibility();
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<RegionHeaderModel> {

      void onCheckBoxAllRegionsPressedEvent(boolean isChecked);

      void toggleVisibility();
   }
}

