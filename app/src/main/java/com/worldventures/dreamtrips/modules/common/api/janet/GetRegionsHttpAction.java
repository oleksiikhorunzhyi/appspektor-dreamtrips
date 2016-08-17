package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/regions", method = HttpAction.Method.GET)
public class GetRegionsHttpAction extends AuthorizedHttpAction {

   @Response List<RegionModel> regionModels;

   public List<RegionModel> getRegionModels() {
      return regionModels;
   }

}