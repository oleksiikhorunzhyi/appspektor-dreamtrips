package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlMerchantsPresenter extends DtlPresenter<DtlMerchantsScreen, ViewState.EMPTY> {

   void locationChangeRequested();

   void applySearch(String query);

   void merchantClicked(ThinMerchant merchant);

   void onOfferClick(ThinMerchant merchant, Offer offer);

   void retryLoadMerchant();

   void mapClicked();
}
