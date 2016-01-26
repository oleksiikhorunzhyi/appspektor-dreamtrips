package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlLocationsPresenter extends JobPresenter<DtlLocationsPresenter.View>
        implements LocationDelegate.LocationListener {

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantRepository dtlMerchantRepository;
    @Inject
    LocationDelegate gpsLocationDelegate;
    //
    ArrayList<DtlLocation> dtlLocations = new ArrayList<>();
    @State
    Status status = Status.NEARBY;
    @State
    String query;
    @State
    Location userGpsLocation;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        connectLocationsExecutor();
        connectLocationsSearchExecutor();
        //
        apiErrorPresenter.setView(view);
        gpsLocationDelegate.attachListener(this);
        //
        if (status.equals(Status.NEARBY)) {
            if (dtlLocations.isEmpty()) {
                if (userGpsLocation == null) {
                    gpsLocationDelegate.tryRequestLocation();
                    view.showGpsObtainingProgress();
                }
            } else {
                view.hideProgress();
                setItems(dtlLocations);
            }
        } else {
            view.hideProgress();
        }
    }

    @Override
    public void dropView() {
        gpsLocationDelegate.detachListener(this);
        super.dropView();
    }

    private void setItems(List<DtlLocation> locations) {
        if (status == Status.NEARBY)
            view.setEmptyViewVisibility(locations.isEmpty());
        else if (TextUtils.isEmpty(query))
            view.setEmptyViewVisibility(dtlLocations.isEmpty());
        else view.setEmptyViewVisibility(false);
        //
        view.setItems(locations);
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
    // Nearby stuff
    ///////////////////////////////////////////////////////////////////////////

    private void connectLocationsExecutor() {
        bindJobCached(dtlLocationManager.nearbyLocationExecutor)
                .onProgress(view::showLocationsObtainingProgress)
                .onError(this::onLocationsFailed)
                .onSuccess(this::onLocationsLoaded);
    }

    @Override
    public void onLocationObtained(Location location) {
        if (location != null) {
            userGpsLocation = location;
            dtlLocationManager.loadNearbyLocations(userGpsLocation);
        } else {
            view.hideProgress();
            view.showSearch();
        }
    }

    public void onLocationsLoaded(List<DtlLocation> locations) {
        if (locations.isEmpty()) view.showSearch();
        else if (dtlLocationManager.getSelectedLocation() == null)
            selectNearest(locations, userGpsLocation);
        else showLoadedLocations(locations);
    }

    public void onLocationsFailed(Throwable exception) {
        apiErrorPresenter.handleError(exception);
    }

    private void showLoadedLocations(List<DtlLocation> locations) {
        view.hideProgress();
        dtlLocations.clear();
        dtlLocations.addAll(locations);
        setItems(dtlLocations);
    }

    private void selectNearest(List<DtlLocation> dtlLocations, Location currentGpsLocation) {
        DtlLocation dtlLocation = Queryable.from(dtlLocations)
                .min(new DtlLocation.DtlNearestComparator(currentGpsLocation));
        onLocationSelected(dtlLocation);
    }

    public void onLocationSelected(DtlLocation location) {
        trackLocationSelection(dtlLocationManager.getSelectedLocation(), location);
        dtlLocationManager.persistLocation(location);
        dtlMerchantRepository.clean();
        view.navigateToMerchants();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search stuff
    ///////////////////////////////////////////////////////////////////////////

    private void connectLocationsSearchExecutor() {
        bindJobCached(dtlLocationManager.searchLocationExecutor)
                .onProgress(this::onSearchStarted)
                .onError(this::onSearchError)
                .onSuccess(this::onSearchFinished);
        //
        if (status == Status.SEARCH) dtlLocationManager.searchLocations(query);
    }

    public void searchOpened() {
        status = Status.SEARCH;
        setItems(Collections.EMPTY_LIST);
    }

    public void searchClosed() {
        status = Status.NEARBY;
        view.hideProgress();
        setItems(dtlLocations);
    }

    public void search(String query) {
        this.query = query;
        dtlLocationManager.searchLocations(query);
    }

    public void onSearchStarted() {
        view.showEmptyProgress();
    }

    public void onSearchFinished(List<DtlLocation> locations) {
        view.hideProgress();
        setItems(locations);
    }

    public void onSearchError(Throwable e) {
        if (e instanceof DtApiException) apiErrorPresenter.handleError(e);
    }

    public interface View extends RxView, ApiErrorView {

        void setItems(List<DtlLocation> dtlLocations);

        void showGpsObtainingProgress();

        void showLocationsObtainingProgress();

        void showEmptyProgress();

        void hideProgress();

        void showSearch();

        void navigateToMerchants();

        void setEmptyViewVisibility(boolean visible);
    }

    public enum Status {
        SEARCH, NEARBY
    }
}
