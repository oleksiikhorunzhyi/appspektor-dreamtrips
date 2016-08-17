package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/trips")
public class GetTripsHttpAction extends AuthorizedHttpAction {
   @Response List<TripModel> response;

   public List<TripModel> getResponse() {
      return this.response;
   }
}