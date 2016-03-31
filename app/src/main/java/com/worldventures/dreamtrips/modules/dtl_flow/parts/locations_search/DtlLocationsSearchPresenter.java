package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenter;

public interface DtlLocationsSearchPresenter
        extends FlowPresenter<DtlLocationsSearchScreen, DtlLocationsSearchViewState> {

    void searchClosed();

    void search(String query);

    void onLocationSelected(DtlExternalLocation location);
}
