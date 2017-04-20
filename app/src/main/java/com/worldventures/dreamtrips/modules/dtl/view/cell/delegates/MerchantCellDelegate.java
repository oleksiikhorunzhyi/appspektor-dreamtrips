package com.worldventures.dreamtrips.modules.dtl.view.cell.delegates;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

public interface MerchantCellDelegate extends ExpandableCellDelegate<ImmutableThinMerchant> {

   void onOfferClick(ThinMerchant merchant, Offer offer);
}
