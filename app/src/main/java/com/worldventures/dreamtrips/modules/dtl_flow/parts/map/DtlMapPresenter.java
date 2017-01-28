package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.List;

public interface DtlMapPresenter extends DtlPresenter<DtlMapScreen, ViewState.EMPTY> {

   void onMapLoaded();

   void applySearch(String query);

   void locationChangeRequested();

   void onMarkerPopupDismiss();

   void onMarkerFocused();

   void onLoadMerchantsClick(LatLng cameraPosition);

   void onListClicked();

   void onLoadMoreClicked();

   void onMarkerClicked(ThinMerchant merchant);

   void retryLoadMerchant();

   void offersOnlySwitched(boolean isOffersOnly);

   void onLoadMerchantsType(List<String> merchantType);

   void loadAmenities(List<String> merchantType);
}
