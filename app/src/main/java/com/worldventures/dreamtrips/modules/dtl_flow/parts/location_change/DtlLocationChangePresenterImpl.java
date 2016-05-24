package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.content.Context;
import android.location.Location;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
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
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class DtlLocationChangePresenterImpl extends DtlPresenterImpl<DtlLocationChangeScreen, ViewState.EMPTY>
        implements DtlLocationChangePresenter {

    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlTransactionService pipesHolder;
    @Inject
    DtlLocationService locationService;
    @Inject
    DtlMerchantService merchantService;
    //
    @State
    ScreenMode screenMode = ScreenMode.NEARBY_LOCATIONS;
    @State
    ArrayList<DtlExternalLocation> dtlNearbyLocations = new ArrayList<>();
    @State
    boolean toolbarInitialized;
    //
    private Subscription locationRequestNoFallback;
    //

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
        // remember this observable - we will start listening to search below only after this fires
        Observable<DtlLocation> locationObservable = connectDtlLocationUpdate();
        //
        connectNearbyLocations();
        connectLocationsSearch();
        connectLocationDelegateNoFallback();
        connectToolbarMapClicks();
        connectToolbarCollapses();
        connectToolbarLocationSearch(locationObservable.take(1));
    }

    private void connectToolbarMapClicks() {
        getView().provideMapClickObservable()
                .compose(bindView())
                .subscribe(aVoid -> mapClicked());
    }

    private void connectToolbarCollapses() {
        getView().provideDtlToolbarCollapsesObservable()
                .compose(bindView())
                .subscribe(aVoid -> navigateAway());
        // below: treat merchant search input focus gain as location search exit (collapsing)
        getView().provideMerchantInputFocusLossObservable()
                .compose(bindView())
                .subscribe(aVoid -> navigateAway());
    }

    private void connectToolbarLocationSearch(Observable<DtlLocation> dtlLocationObservable) {
        getView().provideLocationSearchObservable()
                .skipUntil(dtlLocationObservable)
                .debounce(250L, TimeUnit.MILLISECONDS)
                .compose(bindView())
                .subscribe(this::search);
    }

    private Observable<DtlLocation> connectDtlLocationUpdate() {
        Observable<DtlLocation> locationObservable = locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer());
        Observable.combineLatest(
                locationObservable,
                filterService.getFilterData().map(DtlFilterData::getSearchQuery),
                Pair::new
        ).take(1).subscribe(pair -> {
            getView().updateToolbarTitle(pair.first, pair.second);
            toolbarInitialized = true;
        });
        return locationObservable;
    }

    private void connectLocationDelegateNoFallback() {
        locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
                .compose(bindViewIoToMainComposer())
                .timeout(10L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::onLocationObtained, throwable -> {
                    if (throwable instanceof TimeoutException) getView().hideProgress();
                });
    }

    private void onLocationObtained(Location location) {
        switch (screenMode) {
            case NEARBY_LOCATIONS:
                locationService.nearbyLocationPipe().send(new DtlNearbyLocationAction(location));
                break;
            case AUTO_NEAR_ME:
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                        .build();
                locationService.locationPipe().send(DtlLocationCommand.change(dtlLocation));
                filterService.filterMerchantsActionPipe().clearReplays();
                navigateAway();
                break;
            case SEARCH:
                break;
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
        locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())

                .filter(command -> command.getResult().getLocationSourceType() == LocationSourceType.NEAR_ME)
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> getView().hideNearMeButton());
    }

    private void connectLocationsSearch() {
        locationService.searchLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(DtlSearchLocationAction action) {
        getView().setItems(action.getResult());
    }

    private void mapClicked() {
        History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext())));
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void navigateAway() {
        History history = History.single(new DtlMerchantsPath()); // TODO :: 4/28/16 proper previous screen
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void search(String query) {
        screenMode = ScreenMode.SEARCH;
        locationService.searchLocationPipe().cancelLatest();
        locationService.searchLocationPipe().send(new DtlSearchLocationAction(query.trim()));
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
        locationService.nearbyLocationPipe().observeWithReplay()
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
    public void locationSelected(DtlExternalLocation dtlExternalLocation) {
//        trackLocationSelection(location); // TODO :: 4/20/16 new analytics
        locationService.locationPipe().send(DtlLocationCommand.change(dtlExternalLocation));
        filterService.filterMerchantsActionPipe().clearReplays();
        merchantService.merchantsActionPipe().send(DtlMerchantsAction.load(dtlExternalLocation.getCoordinates().asAndroidLocation()));
        navigateAway();
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
