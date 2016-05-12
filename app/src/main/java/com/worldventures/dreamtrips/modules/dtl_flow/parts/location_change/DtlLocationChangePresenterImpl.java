package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.content.Context;
import android.location.Location;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.DtApiException;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
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
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class DtlLocationChangePresenterImpl extends DtlPresenterImpl<DtlLocationChangeScreen, ViewState.EMPTY>
        implements DtlLocationChangePresenter {

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    DtlFilterMerchantStore filterMerchantStore;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    Janet janet;
    @Inject
    DtlFilterMerchantStore filteredMerchantStore;
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
    private WriteActionPipe<DtlMerchantStoreAction> merchantStoreActionPipe;

    public DtlLocationChangePresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
        merchantStoreActionPipe = janet.createPipe(DtlMerchantStoreAction.class);
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
        Observable<DtlLocation> locationObservable = dtlLocationManager.getSelectedLocation()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer());
        Observable.combineLatest(
                locationObservable,
                filteredMerchantStore.getFilterDataState().map(DtlFilterData::getSearchQuery),
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
                dtlLocationManager.loadNearbyLocations(location);
                break;
            case AUTO_NEAR_ME:
                DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                        .locationSourceType(LocationSourceType.NEAR_ME)
                        .longName(context.getString(R.string.dtl_near_me_caption))
                        .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                        .build();
                dtlLocationManager.persistLocation(dtlLocation);
                filterMerchantStore.filteredMerchantsChangesPipe().clearReplays();
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
        dtlLocationManager.getSelectedLocation()
                .filter(command -> command.getResult().getLocationSourceType() == LocationSourceType.NEAR_ME)
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> getView().hideNearMeButton());
    }

    private void connectLocationsSearch() {
        dtlLocationManager.searchLocationPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail((command, throwable) -> onSearchError(throwable))
                        .onSuccess(this::onSearchFinished));
    }

    private void onSearchFinished(DtlSearchLocationAction command) {
        getView().setItems(command.getResult());
    }

    private void mapClicked() {
        History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext())));
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void navigateAway() {
        History history = History.single(new DtlMerchantsPath()); // TODO :: 4/28/16 proper previous screen
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    public void onSearchError(Throwable e) {
        // TODO :: 3/16/16 TEMPORARY NOT TO BULK USER WITH ERRORS
        // TODO :: 3/16/16 RELATED TO DtlLocationManager bug:
        // when we perform local search. e.g. we enter 4th symbol right after 3rd, when API-call is
        // still going - we get "Smth went wrong error" and then it presents loading results as expected
        if (e instanceof DtApiException) apiErrorPresenter.handleError(e);
    }

    private void search(String query) {
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
                .subscribe(new ActionStateSubscriber<DtlNearbyLocationAction>()
                        .onStart(command -> getView().showProgress())
                        .onFail((command, throwable) -> apiErrorPresenter.handleError(throwable))
                        .onSuccess(this::onLocationsLoaded));
    }

    private void onLocationsLoaded(DtlNearbyLocationAction command) {
        getView().hideProgress();
        showLoadedLocations(command.getResult());
    }

    private void showLoadedLocations(List<DtlExternalLocation> locations) {
        dtlNearbyLocations.clear();
        dtlNearbyLocations.addAll(locations);
        getView().setItems(locations);
    }

    @Override
    public void locationSelected(DtlExternalLocation dtlExternalLocation) {
//        trackLocationSelection(location); // TODO :: 4/20/16 new analytics
        dtlLocationManager.persistLocation(dtlExternalLocation);
        filterMerchantStore.filteredMerchantsChangesPipe().clearReplays();
        merchantStoreActionPipe.send(DtlMerchantStoreAction.load(dtlExternalLocation.getCoordinates().asAndroidLocation()));
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
