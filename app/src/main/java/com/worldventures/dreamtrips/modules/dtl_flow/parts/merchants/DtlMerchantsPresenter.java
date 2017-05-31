package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

import java.util.List;

public interface DtlMerchantsPresenter extends DtlPresenter<DtlMerchantsScreen, DtlMerchantsState> {

   void refresh();

   void loadNext();

   void onRetryMerchantsClick();

   void onRetryDialogDismiss();

   void locationChangeRequested();

   void applySearch(String query);

   void merchantClicked(ThinMerchant merchant);

   void onOfferClick(ThinMerchant merchant, Offer offer);

   void onToggleExpand(boolean expand, ThinMerchant merchant);

   void onRetryMerchantClick();

   void mapClicked();

   void offersOnlySwitched(boolean isOffersOnly);

   void onLoadMerchantsType(List<String> merchantType);

   void loadAmenities(List<String> merchantType);

   void sendToRatingReview(ThinMerchant merchant);

   void setMerchantType(List<String> merchantType, String searchQuery);
}
