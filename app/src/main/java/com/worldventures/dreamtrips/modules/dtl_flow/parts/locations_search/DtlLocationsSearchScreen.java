package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlLocationsSearchScreen extends DtlScreen {

   void setItems(List<DtlExternalLocation> dtlExternalLocations);

   void showProgress();

   void hideProgress();

   void toggleDefaultCaptionVisibility(boolean visible);
}
