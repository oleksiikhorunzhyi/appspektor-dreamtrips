package com.worldventures.dreamtrips.modules.dtl.store;

import android.location.Location;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

public class DtlLocationStore extends RequestingCachingBaseStore {

    private DtlLocation currentLocation;
    //
    private List<LocationsLoadedListener> loadedListeners = new ArrayList<>();

    public DtlLocationStore(SnappyRepository db) {
        super(db);
        currentLocation = db.getSelectedDtlLocation();
    }

    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    public void loadNearbyLocations(Location userLocation) {
        // TODO : handle failure
        checkState();
        requestingPresenter.doRequest(new GetDtlLocationsQuery(userLocation), this::onLocationsLoaded);
    }

    public void persistLocation(DtlLocation location) {
        if (currentLocation == null || !location.getId().equals(currentLocation.getId())) {
            currentLocation = location;
            db.saveSelectedDtlLocation(location);
            db.clearMerchantData();
        }
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
    }
}
