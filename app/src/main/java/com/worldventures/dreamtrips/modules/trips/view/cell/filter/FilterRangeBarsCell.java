package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_filters)
public class FilterRangeBarsCell extends BaseAbstractDelegateCell<FilterModel, FilterRangeBarsCell.Delegate> {

   @InjectView(R.id.rangeBarDay) RangeBar rangeBarDay;
   @InjectView(R.id.rangeBarPrice) RangeBar rangeBarPrice;

   public FilterRangeBarsCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      rangeBarDay.setTickSkipDrawEvery(3);
      rangeBarDay.setOnRangeBarChangeListener((rangeBar, indexLeft, indexRight, valueLeft, valueRight) -> {
         int minNights = Integer.valueOf(valueLeft);
         int maxNights = indexRight == (rangeBarDay.getTickCount() - 1) ? TripsFilterData.Companion.getMAX_NIGHTS() : Integer.valueOf(valueRight);
         cellDelegate.rangeBarDurationEvent(minNights, maxNights);
      });
      rangeBarPrice.setOnRangeBarChangeListener((rangeBar, indexLeft, indexRight, valueLeft, valueRight) -> {
         double minPrice = Double.valueOf(valueLeft);
         double maxPrice = indexRight == (rangeBarPrice.getTickCount() - 1) ? TripsFilterData.Companion.getMAX_PRICE() : Double.valueOf(valueRight);
         cellDelegate.rangeBarPriceEvent(minPrice, maxPrice);
      });

      this.rangeBarDay.setRangePinsByIndices(getModelObject().getIndexLeftDuration(), getModelObject().getIndexRightDuration());
      this.rangeBarPrice.setRangePinsByIndices(getModelObject().getIndexLeftPrice(), getModelObject().getIndexRightPrice());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   public interface Delegate extends CellDelegate<FilterModel> {

      void rangeBarDurationEvent(int minNights, int maxNights);

      void rangeBarPriceEvent(double minPrice, double maxPrice);
   }
}
