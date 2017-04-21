package com.worldventures.dreamtrips.modules.dtl.view.cell;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

public interface DtlMerchantCellDelegate extends CellDelegate<ImmutableThinMerchant> {

   void onExpandedToggle(int position);

   void onOfferClick(ThinMerchant merchant, Offer offer);
}
