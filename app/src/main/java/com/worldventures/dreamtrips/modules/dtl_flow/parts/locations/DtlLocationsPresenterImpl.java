package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import android.content.Context;
import android.location.Location;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import icepick.State;
import rx.Subscription;
import techery.io.library.JobSubscriber;

public class DtlLocationsPresenterImpl extends FlowPresenterImpl<DtlLocationsScreen, ViewState.EMPTY>
        implements DtlLocationsPresenter {

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

    public DtlLocationsPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        //
        tryHideNearMeButton();
        //
        connectNearbyLocationsExecutor();
        //
        locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .timeout(15, TimeUnit.SECONDS)
                .subscribe(this::onLocationObtained, throwable -> {});
    }

    @Override
    public void onLocationResolutionGranted() {
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(new IoToMainComposer<>())
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    @Override
    public void onLocationResolutionDenied() {
        getView().hideProgress();
    }

    @Override
    public void loadNearMeRequested() {
        screenMode = ScreenMode.AUTO_NEAR_ME;
        //
        if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
            locationRequestNoFallback.unsubscribe();
        //
        gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .doOnSubscribe(getView()::showProgress)
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    private void onLocationObtained(Location location) {
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
                Flow.get(getContext()).set(new DtlMerchantsPath());
                break;
        }
    }

    private void tryHideNearMeButton() {
        if (dtlLocationManager.getSelectedLocation() != null &&
                dtlLocationManager.getSelectedLocation().getLocationSourceType() == LocationSourceType.NEAR_ME)
            getView().hideNearMeButton();
    }

    /**
     * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
     * @param e exception that {@link LocationDelegate} subscription returned
     */
    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
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
        dtlLocationManager.nearbyLocationExecutor.connectWithCache()
                .compose(bindViewIoToMainComposer())
                .subscribe(new JobSubscriber<List<DtlExternalLocation>>()
                        .onProgress(getView()::showProgress)
                        .onError(apiErrorPresenter::handleError)
                        .onSuccess(this::onLocationsLoaded));
    }

    private void onLocationsLoaded(List<DtlExternalLocation> locations) {
        getView().hideProgress();
        showLoadedLocations(locations);
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        getView().setItems(locations);
    }

    @Override
    public void onLocationSelected(DtlExternalLocation location) {
        trackLocationSelection(location);
        dtlLocationManager.persistLocation(location);
        dtlMerchantManager.clean();
        Flow.get(getContext()).set(new DtlMerchantsPath());
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
