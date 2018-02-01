package com.worldventures.dreamtrips.modules.trips.view.cell;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.config.model.TravelBannerRequirement;

public interface BannerCellDelegate extends CellDelegate<TravelBannerRequirement> {

   void onCancelClicked();
}
