package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.List;
import java.util.Locale;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/locations")
public class NearbyLocationsHttpAction extends AuthorizedHttpAction {

   @Query("ll") String latLng;

   @Response List<DtlExternalLocation> response;

   public NearbyLocationsHttpAction(Location location) {
      this.latLng = String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }

   public List<DtlExternalLocation> getResult() {
      return response;
   }
}
