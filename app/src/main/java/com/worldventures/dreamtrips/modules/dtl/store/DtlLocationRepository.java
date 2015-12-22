package com.worldventures.dreamtrips.modules.dtl.store;

import android.location.Location;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

public class DtlLocationRepository extends RequestingCachingBaseStore {

    private DtlLocation currentLocation;
    //
    private List<LocationsLoadedListener> loadedListeners = new ArrayList<>();

    public DtlLocationRepository(SnappyRepository db) {
        super(db);
    }

    public void loadNearbyLocations(Location userLocation) {
        checkState();
        requestingPresenter.doRequest(new GetDtlLocationsQuery(userLocation),
                this::onLocationsLoaded, this::onLocationsLoadingFailed);
    }

    public void persistLocation(DtlLocation location) {
        if (currentLocation == null || !location.getId().equals(currentLocation.getId())) {
            currentLocation = location;
        }
    }

    private void onLocationsLoadingFailed(SpiceException spiceException) {
        checkListeners(loadedListeners);
        Queryable.from(loadedListeners).forEachR(listener -> listener.onLocationsFailed(spiceException));
    }

    private void onLocationsLoaded(List<DtlLocation> locations) {
        checkListeners(loadedListeners);
        Queryable.from(loadedListeners).forEachR(listener -> listener.onLocationsLoaded(locations));
    }

    @Nullable
    public DtlLocation getSelectedLocation() {
        return currentLocation;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    public void attachListener(LocationsLoadedListener listener) {
        this.loadedListeners.add(listener);
    }

    public void detachListener(LocationsLoadedListener listener) {
        this.loadedListeners.remove(listener);
    }

    public interface LocationsLoadedListener {

        void onLocationsLoaded(List<DtlLocation> locations);

        void onLocationsFailed(SpiceException exception);
    }
}
