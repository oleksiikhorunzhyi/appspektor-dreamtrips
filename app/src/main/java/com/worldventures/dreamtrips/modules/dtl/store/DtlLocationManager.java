package com.worldventures.dreamtrips.modules.dtl.store;

import android.content.Context;
import android.location.Location;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateLocationAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class DtlLocationManager {

    @Inject
    SnappyRepository db;
    @ForApplication
    @Inject
    Context context;
    @Inject
    RetryLoginComposer retryLoginComposer;
    @Inject
    Janet janet;

    private ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;
    private ActionPipe<DtlSearchLocationAction> searchLocationPipe;
    private ActionPipe<DtlUpdateLocationAction> updateLocationPipe;
    private ActionPipe<DtlLocationCommand> locationPipe;

    public DtlLocationManager(Injector injector) {
        injector.inject(this);
        init(Schedulers.io());
    }

    public DtlLocationManager(Janet janet, SnappyRepository db, Scheduler scheduler) {
        this.db = db;
        this.janet = janet;
        init(scheduler);
    }

    private void init(Scheduler scheduler) {
        nearbyLocationPipe = janet.createPipe(DtlNearbyLocationAction.class, scheduler);
        updateLocationPipe = janet.createPipe(DtlUpdateLocationAction.class, scheduler);
        locationPipe = janet.createPipe(DtlLocationCommand.class, scheduler);
        searchLocationPipe = janet.createPipe(DtlSearchLocationAction.class, scheduler);
        locationPipe.send(new DtlLocationCommand(db));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby
    ///////////////////////////////////////////////////////////////////////////


    public ReadActionPipe<DtlNearbyLocationAction> nearbyLocationPipe() {
        return nearbyLocationPipe.asReadOnly();
    }

    @SuppressWarnings("unchecked")
    public void loadNearbyLocations(Location location) {
        nearbyLocationPipe.send(new DtlNearbyLocationAction(location));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public ReadActionPipe<DtlSearchLocationAction> searchLocationPipe() {
        return searchLocationPipe.asReadOnly();
    }

    @SuppressWarnings("unchecked")
    public void searchLocations(String query) {
        nearbyLocationPipe.cancelLatest();
        searchLocationPipe.cancelLatest();
        searchLocationPipe.send(new DtlSearchLocationAction(query));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Persisted location
    ///////////////////////////////////////////////////////////////////////////


    public void cleanLocation() {
        locationPipe.send(new DtlLocationCommand(DtlLocation.UNDEFINED));
        updateLocationPipe.send(new DtlUpdateLocationAction(db, DtlLocation.UNDEFINED));
    }

    public void persistLocation(DtlLocation location) {
        locationPipe.send(new DtlLocationCommand(location));
        updateLocationPipe.send(new DtlUpdateLocationAction(db, location));
    }

    public Observable<DtlLocationCommand> getSelectedLocation() {
        return locationPipe.createObservableSuccess(new DtlLocationCommand(db));
    }

}
