package com.worldventures.dreamtrips.modules.trips.view.cell;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public interface TripCellDelegate extends CellDelegate<TripModel> {

   void onLikeClicked(TripModel tripModel);

   void onAddToBucketClicked(TripModel tripModel);
}
