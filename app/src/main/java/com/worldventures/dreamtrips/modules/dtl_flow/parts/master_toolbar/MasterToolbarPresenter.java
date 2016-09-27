package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

public interface MasterToolbarPresenter extends DtlPresenter<MasterToolbarScreen, MasterToolbarState> {

   void applySearch(String query);

   void loadNearMeRequested();

   void locationSelected(DtlExternalLocation location);

   void onLocationResolutionGranted();

   void onLocationResolutionDenied();

   void onShowToolbar();

   boolean needShowAutodetectButton();

   void offersOnlySwitched(boolean isOffersOnly);
}
