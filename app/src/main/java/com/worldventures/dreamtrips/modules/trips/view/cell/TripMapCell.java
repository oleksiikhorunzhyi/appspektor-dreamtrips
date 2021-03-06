package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.util.TripMapViewInjector;

@Layout(R.layout.adapter_item_trip_map)
public class TripMapCell extends BaseAbstractDelegateCell<TripModel, CellDelegate<TripModel>> {

   public TripMapCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      int coverSize = itemView.getResources().getDimensionPixelSize(R.dimen.map_trip_detail_cover_size);
      TripMapViewInjector tripMapViewInjector = new TripMapViewInjector(itemView, coverSize);
      tripMapViewInjector.initTripData(getModelObject());
      itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
