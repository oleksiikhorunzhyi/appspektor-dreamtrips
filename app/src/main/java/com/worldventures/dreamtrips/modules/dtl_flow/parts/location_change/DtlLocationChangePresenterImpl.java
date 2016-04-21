package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.content.Context;
import android.location.Location;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import flow.Flow;
import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;

public class DtlLocationChangePresenterImpl extends DtlPresenterImpl<DtlLocationChangeScreen, ViewState.EMPTY>
    implements DtlLocationChangePresenter {

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

    public DtlLocationChangePresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        apiErrorPresenter.setView(getView());
        //
        tryHideNearMeButton();
        connectNearbyLocations();
        connectLocationsSearch();
        //
        locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .timeout(10L, TimeUnit.SECONDS)
                .subscribe(this::onLocationObtained, throwable -> {
                    if (throwable instanceof TimeoutException) getView().hideProgress();
                });
        //
        dtlLocationManager.getSelectedLocation()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> getView().updateToolbarTitle(location,
                        dtlMerchantManager.getCurrentQuery()));
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
                dtlMerchantManager.clean();
                Flow.get(getContext()).goBack();
                break;
            case SEARCH: break;
        }
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

    private void tryHideNearMeButton() {
        dtlLocationManager.getSelectedLocation()
                .filter(command -> command.getResult().getLocationSourceType() == LocationSourceType.NEAR_ME)
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> getView().hideNearMeButton());
    }

    private void connectLocationsSearch() {
        dtlLocationManager.searchLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlSearchLocationCommand>()
                        .onStart(command -> getView().showProgress())
                        .onFail((command, throwable) -> apiErrorPresenter.handleError(throwable))
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(DtlSearchLocationCommand command) {
        getView().setItems(command.getResult());
    }

    @Override
    public void toolbarCollapsed() {
        Flow.get(getContext()).goBack();
    }

    @Override
    public void search(String query) {
        screenMode = ScreenMode.SEARCH;
        dtlLocationManager.searchLocations(query);
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

    /**
     * Check if given error's cause is insufficient GPS resolution
     * or usual throwable and act accordingly
     *
     * @param e exception that {@link LocationDelegate} returned
     */
    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            getView().locationResolutionRequired(
                    ((LocationDelegate.LocationException) e).getStatus());
        else onLocationResolutionDenied();
    }

    private void connectNearbyLocations() {
        dtlLocationManager.nearbyLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlNearbyLocationCommand>()
                        .onStart(command -> getView().showProgress())
                        .onFail((command, throwable) -> apiErrorPresenter.handleError(throwable))
                        .onSuccess(this::onLocationsLoaded));
    }

    private void onLocationsLoaded(DtlNearbyLocationCommand command) {
        getView().hideProgress();
        showLoadedLocations(command.getResult());
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        getView().setItems(locations);
    }

    @Override
    public void locationSelected(DtlExternalLocation location) {
//        trackLocationSelection(location); // TODO :: 4/20/16 new analytics
        dtlLocationManager.persistLocation(location);
        dtlMerchantManager.clean();
        Flow.get(getContext()).goBack();
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
        /**
         * User requested search and is currently viewing results / waiting for progress.
         */
        SEARCH,
    }
}
