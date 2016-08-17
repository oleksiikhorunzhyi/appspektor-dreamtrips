package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlLocationsScreen extends DtlScreen {

   void locationResolutionRequired(Status status);

   void setItems(List<DtlExternalLocation> dtlExternalLocations);

   void hideNearMeButton();

   void showProgress();

   void hideProgress();
}
