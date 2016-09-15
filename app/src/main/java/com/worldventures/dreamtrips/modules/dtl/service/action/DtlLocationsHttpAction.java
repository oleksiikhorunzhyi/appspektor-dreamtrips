package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/locations")
public class DtlLocationsHttpAction extends AuthorizedHttpAction {

   @Query("query") String query;

   @Response List<DtlExternalLocation> response = new ArrayList<>();

   public DtlLocationsHttpAction(String query) {
      this.query = query;
   }

   public List<DtlExternalLocation> getResponse() {
      return response;
   }
}
