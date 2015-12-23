package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlLocationSearchDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationRepository;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View>
        implements DtlLocationRepository.LocationsLoadedListener, LocationDelegate.LocationListener,
        DtlLocationSearchDelegate.Listener {

    @Inject
    DtlLocationRepository dtlLocationRepository;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    //
    private DtlLocationSearchDelegate searchDelegate;
    //
    @State
    ArrayList<DtlLocation> dtlLocations = new ArrayList<>();
    @State
    Status status = Status.NEARBY;
    //
    private Location userGpsLocation;
    @Inject
    LocationDelegate gpsLocationDelegate;

    @Override
    public void onInjected() {
        super.onInjected();
        dtlLocationRepository.setRequestingPresenter(this);
        searchDelegate = new DtlLocationSearchDelegate();
        searchDelegate.setRequestingPresenter(this);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);
        searchDelegate.saveInstanceState(outState);
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        searchDelegate.restoreInstanceState(savedState);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        dtlLocationRepository.attachListener(this);
        gpsLocationDelegate.attachListener(this);
        searchDelegate.attachListener(this);
        //
        if (status.equals(Status.NEARBY)) {
            if (dtlLocations.isEmpty()) {
                gpsLocationDelegate.tryRequestLocation();
                view.showGpsObtainingProgress();
            }
            else {
                view.hideProgress();
                view.setItems(dtlLocations);
            }
        } else {
            view.hideProgress();
            searchDelegate.requestSavedSearchResults();
        }
    }

    @Override
    public void dropView() {
        gpsLocationDelegate.detachListener(this);
        dtlLocationRepository.detachListener(this);
        searchDelegate.detachListener();
        searchDelegate.unsetRequestingPresenter();
        dtlLocationRepository.detachRequestingPresenter();
        super.dropView();
    }

    @Override
    public void onLocationObtained(Location location) {
        if (location != null) {
            userGpsLocation = location;
            view.showLocationsObtainingProgress();
            dtlLocationRepository.loadNearbyLocations(userGpsLocation);
        } else {
            view.hideProgress();
            view.showSearch();
        }
    }

    @Override
    public void onLocationsLoaded(List<DtlLocation> locations) {
        if (locations.isEmpty()) {
            view.showSearch();
            return;
        } else if (dtlLocationRepository.getSelectedLocation() == null) {
            selectNearest(locations, userGpsLocation);
        } else {
            showLoadedLocations(locations);
        }
    }

    private void showLoadedLocations(List<DtlLocation> locations) {
        view.hideProgress();
        dtlLocations.clear();
        dtlLocations.addAll(locations);
        view.setItems(dtlLocations);
    }

    @Override
    public void onLocationsFailed(SpiceException exception) {
        handleError(exception);
    }

    private void selectNearest(List<DtlLocation> dtlLocations, Location currentGpsLocation) {
        DtlLocation dtlLocation = Queryable.from(dtlLocations)
                .min(new DtlLocation.DtlNearestComparator(currentGpsLocation));
        onLocationSelected(dtlLocation);
    }

    public void onLocationSelected(DtlLocation location) {
        trackLocationSelection(dtlLocationRepository.getSelectedLocation(), location);
        dtlLocationRepository.persistLocation(location);
        dtlMerchantRepository.clean();
        view.navigateToMerchants();
    }

    /**
     * Analytic-related
     */
    private void trackLocationSelection(DtlLocation previousLocation, DtlLocation newLocation) {
        if (previousLocation != null)
            TrackingHelper.dtlChangeLocation(newLocation.getId());
        String locationSelectType = status.equals(Status.NEARBY) ?
                TrackingHelper.DTL_ACTION_SELECT_LOCATION_FROM_NEARBY : TrackingHelper.DTL_ACTION_SELECT_LOCATION_FROM_SEARCH;
        TrackingHelper.dtlSelectLocation(locationSelectType, newLocation.getId());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search stuff
    ///////////////////////////////////////////////////////////////////////////

    public void searchOpened() {
        status = Status.SEARCH;
        view.setItems(Collections.EMPTY_LIST);
    }

    public void searchClosed() {
        status = Status.NEARBY;
        searchDelegate.dismissDelegate();
        view.hideProgress();
        view.setItems(dtlLocations);
    }

    public void search(String query) {
        searchDelegate.performSearch(query);
    }

    @Override
    public void onSearchStarted() {
        view.showEmptyProgress();
    }

    @Override
    public void onSearchFinished(List<DtlLocation> locations) {
        view.hideProgress();
        view.setItems(locations);
    }

    @Override
    public void onSearchError(SpiceException e) {
        handleError(e);
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlLocation> dtlLocations);

        void showGpsObtainingProgress();

        void showLocationsObtainingProgress();

        void showEmptyProgress();

        void hideProgress();

        void showSearch();

        void navigateToMerchants();
    }

    public enum Status {
        SEARCH, NEARBY
    }
}
