package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TripsInteractor {

    private final ActionPipe<GetTripDetailsCommand> detailsPipe;
    private final ActionPipe<GetTripsCommand.ReloadTripsCommand> reloadTripsActionPipe;
    private final ActionPipe<GetTripsCommand.LoadNextTripsCommand> loadNextTripsActionPipe;

    @Inject
    public TripsInteractor(Janet janet) {
        detailsPipe = janet.createPipe(GetTripDetailsCommand.class, Schedulers.io());
        reloadTripsActionPipe = janet.createPipe(GetTripsCommand.ReloadTripsCommand.class, Schedulers.io());
        loadNextTripsActionPipe = janet.createPipe(GetTripsCommand.LoadNextTripsCommand.class, Schedulers.io());
    }

    public ActionPipe<GetTripDetailsCommand> detailsPipe() {
        return detailsPipe;
    }

    public ActionPipe<GetTripsCommand.ReloadTripsCommand> reloadTripsActionPipe() {
        return reloadTripsActionPipe;
    }

    public ActionPipe<GetTripsCommand.LoadNextTripsCommand> loadNextTripsActionPipe() {
        return loadNextTripsActionPipe;
    }
}
