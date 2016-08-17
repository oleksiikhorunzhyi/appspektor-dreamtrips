package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsHttpAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsHttpAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TripMapInteractor {

    private ActionPipe<GetMapObjectsHttpAction> mapObjectsActionPipe;
    private ActionPipe<GetDetailedTripsHttpAction> detailedTripsActionPipe;

    public TripMapInteractor(Janet janet) {
        mapObjectsActionPipe = janet.createPipe(GetMapObjectsHttpAction.class, Schedulers.io());
        detailedTripsActionPipe = janet.createPipe(GetDetailedTripsHttpAction.class, Schedulers.io());
    }

    public ActionPipe<GetMapObjectsHttpAction> mapObjectsPipe() {
        return mapObjectsActionPipe;
    }

    public ActionPipe<GetDetailedTripsHttpAction> detailedTripsPipe() {
        return detailedTripsActionPipe;
    }
}
