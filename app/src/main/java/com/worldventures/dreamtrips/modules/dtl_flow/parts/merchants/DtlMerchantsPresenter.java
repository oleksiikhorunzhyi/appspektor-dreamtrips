package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMerchantsPresenter extends DtlPresenter<DtlMerchantsScreen, DtlMerchantsState> {

   void locationChangeRequested();

   void applySearch(String query);

   void merchantClicked(ThinMerchant merchant);

   void onOfferClick(ThinMerchant merchant, Offer offer);

   void onToggleExpand(boolean expand, ThinMerchant merchant);

   void retryLoadMerchant();

   void mapClicked();

   void offersOnlySwitched(boolean isOffersOnly);
}
