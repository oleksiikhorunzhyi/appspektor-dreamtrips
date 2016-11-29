package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlLocationChangePresenter extends DtlPresenter<DtlLocationChangeScreen, ViewState.EMPTY> {

   void loadNearMeRequested();

   void locationSelected(DtlLocation location);

   void onLocationResolutionGranted();

   void onLocationResolutionDenied();
}
