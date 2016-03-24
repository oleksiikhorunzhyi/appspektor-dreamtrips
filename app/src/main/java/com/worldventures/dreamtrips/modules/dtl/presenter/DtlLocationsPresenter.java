package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import icepick.State;
import rx.Subscription;

public class DtlLocationsPresenter extends JobPresenter<DtlLocationsPresenter.View> {

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    LocationDelegate gpsLocationDelegate;
    //
    @State
    ScreenMode screenMode = ScreenMode.NEARBY_LOCATIONS;
    @State
    ArrayList<DtlExternalLocation> dtlNearbyLocations = new ArrayList<>();
    //
    private Subscription locationRequestNoFallback;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        //
        tryHideNearMeButton();
        //
        connectNearbyLocationsExecutor();
        //
        locationRequestNoFallback = view.bind(gpsLocationDelegate.requestLocationUpdate())
                .compose(new IoToMainComposer<>())
                .timeout(15, TimeUnit.SECONDS)
                .subscribe(this::onLocationObtained, throwable -> {});
    }
    
    public void onLocationResolutionGranted() {
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(new IoToMainComposer<>())
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    public void onLocationResolutionDenied() {
        view.hideProgress();
    }

    public void loadNearMeRequested() {
        screenMode = ScreenMode.AUTO_NEAR_ME;
        //
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        view.bind(gpsLocationDelegate.requestLocationUpdate())
                .compose(new IoToMainComposer<>())
                .subscribe(this::onLocationObtained, this::onLocationError);
        view.showProgress();
    }

    public void onLocationObtained(Location location) {
        switch (screenMode) {
            case NEARBY_LOCATIONS:
                dtlLocationManager.loadNearbyLocations(location);
                break;
            case AUTO_NEAR_ME:
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                        .build();
                dtlLocationManager.persistLocation(dtlLocation);
                view.navigateToMerchants();
                break;
        }
    }

    private void tryHideNearMeButton() {
        if (dtlLocationManager.getSelectedLocation() != null &&
                dtlLocationManager.getSelectedLocation().getLocationSourceType() == LocationSourceType.NEAR_ME)
            view.hideNearMeButton();
    }

    /**
     * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
     * @param e exception that {@link LocationDelegate} subscription returned
     */
    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            view.locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
        else onLocationResolutionDenied();
    }

    /**
     * Analytic-related
     */
    private void trackLocationSelection(DtlExternalLocation newLocation) {
        TrackingHelper.searchLocation(newLocation);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby stuff
    ///////////////////////////////////////////////////////////////////////////

    private void connectNearbyLocationsExecutor() {
        bindJobCached(dtlLocationManager.nearbyLocationExecutor)
                .onProgress(view::showProgress)
                .onError(apiErrorPresenter::handleError)
                .onSuccess(this::onLocationsLoaded);
    }

    public void onLocationsLoaded(List<DtlExternalLocation> locations) {
        view.hideProgress();
        showLoadedLocations(locations);
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        view.setItems(locations);
    }

    public void onLocationSelected(DtlExternalLocation location) {
        trackLocationSelection(location);
        dtlLocationManager.persistLocation(location);
        dtlMerchantManager.clean();
        view.navigateToMerchants();
    }

    public interface View extends RxView, ApiErrorView {

        void locationResolutionRequired(Status status);

        void setItems(List<DtlExternalLocation> dtlExternalLocations);

        void hideNearMeButton();

        void showProgress();

        void hideProgress();

        void navigateToMerchants();
    }

    /**
     * Represents view state of presenter+view - to be used for screen restoration, e.g. after rotate.
     */
    public enum ScreenMode {
        /**
         * System tried to pre-load some locations based on device's current GPS location.<br />
         * Default for current screen.
         */
        NEARBY_LOCATIONS,
        /**
         * User explicitly requested to load merchants by device's GPS location.
         */
        AUTO_NEAR_ME,
    }
}
