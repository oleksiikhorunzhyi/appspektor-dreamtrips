package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;

public interface DtlLocationsSearchPresenter extends DtlPresenter<DtlLocationsSearchScreen, DtlLocationsSearchViewState> {

   void searchClosed();

   void search(String query);

   void onLocationSelected(DtlLocation location);
}
