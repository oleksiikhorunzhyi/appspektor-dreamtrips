package com.worldventures.dreamtrips.modules.trips.command;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TripsInteractor {

    private ActionPipe<GetTripDetailsCommand> detailsPipe;

    @Inject
    public TripsInteractor(Janet janet) {
        detailsPipe = janet.createPipe(GetTripDetailsCommand.class, Schedulers.io());
    }

    public ActionPipe<GetTripDetailsCommand> detailsPipe() {
        return detailsPipe;
    }
}
