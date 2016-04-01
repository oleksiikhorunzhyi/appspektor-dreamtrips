package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlLocationsPresenter extends FlowPresenter<DtlLocationsScreen, ViewState.EMPTY> {

    void loadNearMeRequested();

    void onLocationSelected(DtlExternalLocation location);

    void onLocationResolutionGranted();

    void onLocationResolutionDenied();
}
