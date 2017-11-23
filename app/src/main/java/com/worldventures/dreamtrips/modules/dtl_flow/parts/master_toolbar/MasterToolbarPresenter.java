package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

public interface MasterToolbarPresenter extends DtlPresenter<MasterToolbarScreen, MasterToolbarState> {

   void applySearch(String query);

   void loadNearMeRequested();

   void locationSelected(DtlLocation location, String merchantSearchQuery);

   void onLocationResolutionGranted();

   void onLocationResolutionDenied();

   void onShowToolbar();

   boolean needShowAutodetectButton();

   void offersOnlySwitched(boolean isOffersOnly);

   void onTransactionClicked();
}
