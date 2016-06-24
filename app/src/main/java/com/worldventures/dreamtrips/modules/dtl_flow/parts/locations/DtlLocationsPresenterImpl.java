package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations;

import android.content.Context;
import android.location.Location;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class DtlLocationsPresenterImpl extends DtlPresenterImpl<DtlLocationsScreen, ViewState.EMPTY>
        implements DtlLocationsPresenter {

    @Inject
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlLocationInteractor locationInteractor;
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
        connectNearbyLocations();
        //
        locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .timeout(15, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::onLocationObtained, throwable -> {
                    if (throwable instanceof TimeoutException) getView().hideProgress();
                });
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
                locationInteractor.nearbyLocationPipe().send(new DtlNearbyLocationAction(location));
                break;
            case AUTO_NEAR_ME:
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                        .build();
                locationInteractor.locationPipe().send(DtlLocationCommand.change(dtlLocation));
                navigateToMerchants();
                break;
        }
    }

    private void tryHideNearMeButton() {
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                .filter(command -> command.getResult().getLocationSourceType() == LocationSourceType.NEAR_ME)
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> getView().hideNearMeButton());
    }

    /**
     * Check if given error's cause is insufficient GPS resolution or usual throwable and act accordingly
     *
     * @param e exception that {@link LocationDelegate} subscription returned
     */
    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
        else onLocationResolutionDenied();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nearby stuff
    ///////////////////////////////////////////////////////////////////////////

    private void connectNearbyLocations() {
        locationInteractor.nearbyLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlNearbyLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::onLocationsLoaded));
    }

    private void onLocationsLoaded(DtlNearbyLocationAction action) {
        getView().hideProgress();
        showLoadedLocations(action.getResult());
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        getView().setItems(locations);
    }

    @Override
    public void onLocationSelected(DtlExternalLocation location) {
        locationInteractor.locationPipe().send(DtlLocationCommand.change(location));
        filterInteractor.filterMerchantsActionPipe().clearReplays();
        navigateToMerchants();
    }

    private void navigateToMerchants() {
        History history = History.single(new DtlMerchantsPath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
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
