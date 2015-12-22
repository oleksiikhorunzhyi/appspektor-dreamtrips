package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

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
import retrofit.http.HEAD;

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
    ArrayList<DtlLocation> dtlLocations;
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
        searchDelegate = new DtlLocationSearchDelegate(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        dtlLocationRepository.attachListener(this);
        gpsLocationDelegate.attachListener(this);
        //
        // TODO : handle possible state restoring with searchDelegate
        dtlLocations = new ArrayList<>();
        //
        gpsLocationDelegate.tryRequestLocation();
        //
        view.startLoading();
    }

    @Override
    public void dropView() {
        gpsLocationDelegate.detachListener(this);
        dtlLocationRepository.detachListener(this);
        searchDelegate.detachListener();
        dtlLocationRepository.detachRequestingPresenter();
        super.dropView();
    }

    @Override
    public void onLocationObtained(Location location) {
        if (location != null) {
            userGpsLocation = location;
            view.citiesLoadingStarted();
            dtlLocationRepository.loadNearbyLocations(userGpsLocation);
        } else {
            view.finishLoading();
            view.showSearch();
        }
    }

    @Override
    public void onLocationsLoaded(List<DtlLocation> locations) {
        view.finishLoading();
        //
        dtlLocations.clear();
        dtlLocations.addAll(locations);
        view.setItems(dtlLocations);
        //
        if (dtlLocations.isEmpty()) view.showSearch();
        else if (dtlLocationRepository.getSelectedLocation() == null)
            selectNearest(userGpsLocation);
    }

    @Override
    public void onLocationsFailed(SpiceException exception) {
        handleError(exception);
    }

    private void selectNearest(Location currentLocation) {
        DtlLocation dtlLocation = Queryable.from(dtlLocations)
                .min(new DtlLocation.DtlNearestComparator(currentLocation));
        onLocationSelected(dtlLocation);
    }

    public void onLocationSelected(DtlLocation location) {
        trackLocationSelection(dtlLocationRepository.getSelectedLocation(), location);
        dtlLocationRepository.persistLocation(location);
        dtlMerchantRepository.clean();
        view.showMerchants();
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
        searchDelegate.setNearbyLocations(dtlLocations);
        searchDelegate.attachListener(this);
        view.setItems(Collections.EMPTY_LIST);
    }

    public void searchClosed() {
        status = Status.NEARBY;
        searchDelegate.dismissDelegate();
    }

    public void search(String query) {
        searchDelegate.performSearch(query);
    }

    @Override
    public void onSearchStarted() {
        view.startLoading();
        view.citiesLoadingStarted();
    }

    @Override
    public void onSearchFinished(List<DtlLocation> locations) {
        view.finishLoading();
        view.setItems(locations);
    }

    @Override
    public void onSearchError(SpiceException e) {
        handleError(e);
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlLocation> dtlLocations);

        void startLoading();

        void finishLoading();

        void citiesLoadingStarted();

        void showMerchants();

        void showSearch();
    }

    public enum Status {
        SEARCH, NEARBY
    }
}
