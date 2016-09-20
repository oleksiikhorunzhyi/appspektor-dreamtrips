package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterSoldOutModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class SoldOutCell extends BoolCell<FilterSoldOutModel, SoldOutCell.Delegate> {

   public SoldOutCell(View view) {
      super(view);
   }

   @Override
   public int getTitle() {
      return R.string.filter_show_sold_out;
   }

   @Override
   public void sendEvent(boolean b) {
      cellDelegate.onFilterShowSoldOutEvent(b);
   }

   public interface Delegate extends CellDelegate<FilterSoldOutModel> {
      void onFilterShowSoldOutEvent(boolean enabled);
   }
}
