package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlLocationChangePresenter
        extends DtlPresenter<DtlLocationChangeScreen, ViewState.EMPTY> {

    void loadNearMeRequested();

    void locationSelected(DtlExternalLocation location);

    void onLocationResolutionGranted();

    void onLocationResolutionDenied();

    void toolbarCollapsed();

    void search(String query);
}
