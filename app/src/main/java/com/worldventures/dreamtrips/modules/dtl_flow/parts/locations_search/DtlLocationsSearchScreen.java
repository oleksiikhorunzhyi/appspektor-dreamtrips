package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlLocationsSearchScreen extends DtlScreen {

   void setItems(List<DtlLocation> dtlExternalLocations);

   void showProgress();

   void hideProgress();

   void toggleDefaultCaptionVisibility(boolean visible);
}
