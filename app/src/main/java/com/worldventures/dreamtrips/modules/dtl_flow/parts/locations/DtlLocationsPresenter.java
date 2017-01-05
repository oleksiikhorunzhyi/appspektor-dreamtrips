package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlLocationsPresenter extends DtlPresenter<DtlLocationsScreen, ViewState.EMPTY> {

   void loadNearMeRequested();

   void onLocationSelected(DtlLocation location);

   void onLocationResolutionGranted();

   void onLocationResolutionDenied();
}
