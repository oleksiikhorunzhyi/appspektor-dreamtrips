package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.FilterRecentlyAddedModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class FilterRecentlyAddedCell extends BoolCell<FilterRecentlyAddedModel, FilterRecentlyAddedCell.Delegate> {
   public FilterRecentlyAddedCell(View view) {
      super(view);
   }

   @Override
   public int getTitle() {
      return R.string.filter_recently_added;
   }

   @Override
   public void sendEvent(boolean b) {
      cellDelegate.onFilterShowRecentlyAddedEvent(b);
   }

   public interface Delegate extends CellDelegate<FilterRecentlyAddedModel> {
      void onFilterShowRecentlyAddedEvent(boolean enabled);
   }
}
