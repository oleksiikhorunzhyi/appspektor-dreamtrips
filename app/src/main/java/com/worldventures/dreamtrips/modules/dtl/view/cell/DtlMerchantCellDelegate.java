package com.worldventures.dreamtrips.modules.dtl.view.cell;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

public interface DtlMerchantCellDelegate extends CellDelegate<DtlMerchant> {

   void onExpandedToggle(int position);

   void onOfferClick(DtlMerchant dtlMerchant, Offer dtlOffer);
}
