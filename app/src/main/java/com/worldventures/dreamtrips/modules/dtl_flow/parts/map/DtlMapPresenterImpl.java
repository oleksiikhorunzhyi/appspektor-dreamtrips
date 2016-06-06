package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.JanetPlainActionComposer;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

import static rx.Observable.just;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY> implements DtlMapPresenter {

    public static final int MAX_DISTANCE = 50;

    @Inject
    DtlTransactionService pipesHolder;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    protected Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    Janet janet;
    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlFilterMerchantService filterService;
    @Inject
    DtlLocationService locationService;
    //
    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    public DtlMapPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
        filterService.getFilterData()
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);
        getView().getToggleObservable()
                .subscribe(offersOnly -> filterService.filterDataPipe()
                        .send(DtlFilterDataAction.applyOffersOnly(offersOnly)));
        //
        updateToolbarTitle();
        updateFilterButtonState();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) getView().prepareMap();
    }

    protected void connectService() {
        merchantService.merchantsActionPipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .compose(JanetPlainActionComposer.instance())
                .filter(dtlMerchantsAction -> dtlMerchantsAction.getResult().isEmpty())
                .subscribe(s -> getView().informUser(R.string.dtl_no_merchants_caption),
                        throwable -> {});
        merchantService.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress(true)));
        //
        filterService.filterMerchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> {
                            getView().showProgress(false);
                            apiErrorPresenter.handleActionError(action, throwable);
                        })
                        .onSuccess(action -> onMerchantsLoaded(action.getResult())));
        //
        filterService.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    private void updateToolbarTitle() {
        Observable.combineLatest(
                locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                        .map(DtlLocationCommand::getResult),
                filterService.getFilterData().map(DtlFilterData::getSearchQuery),
                Pair::new
        ).compose(bindViewIoToMainComposer())
//                .take(1)
                .subscribe(pair -> getView().updateToolbarTitle(pair.first, pair.second));
    }

    private void updateFilterButtonState() {
        filterService.getFilterData()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    protected void tryHideMyLocationButton(boolean hide) {
        getView().tryHideMyLocationButton(hide);
    }

    protected Observable<Location> getFirstCenterLocation() {
        return locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                .map(command -> {
                    Location lastPosition = db.getLastMapCameraPosition();
                    boolean validLastPosition = lastPosition != null && lastPosition.getLat() != 0 && lastPosition.getLng() != 0;
                    DtlLocation lastSelectedLocation = command.getResult();
                    return validLastPosition ? lastPosition : (command.isResultDefined() ?
                            lastSelectedLocation.getCoordinates() : new Location(0d, 0d));
                });
    }

    protected Observable<Boolean> showingLoadMerchantsButton() {
        return MapObservableFactory.createCameraChangeObservable(getView().getMap())
                .doOnNext(position -> getView().cameraPositionChange(position))
                .doOnNext(position ->
                        db.saveLastMapCameraPosition(new Location(position.target.latitude,
                                position.target.longitude)))
                .flatMap(position -> {
                    if (position.zoom < MapViewUtils.DEFAULT_ZOOM) {
                        return just(true);
                    }
                    return locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                            .compose(bindViewIoToMainComposer())
                            .map(command -> !DtlLocationHelper.checkLocation(MAX_DISTANCE,
                                    command.getResult().getCoordinates().asLatLng(),
                                    position.target, DistanceType.MILES));
                });
    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        //
        getView().showProgress(false);
        getView().showButtonLoadMerchants(false);
        showPins(dtlMerchants);
        //
        locationService.locationPipe().createObservableSuccess(DtlLocationCommand.last())
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                    if (location.getLocationSourceType() == LocationSourceType.FROM_MAP &&
                            getView().getMap().getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM)
                        getView().zoom(MapViewUtils.DEFAULT_ZOOM);
                    //
                    if (location.getLocationSourceType() != LocationSourceType.NEAR_ME)
                        getView().addLocationMarker(location.getCoordinates().asLatLng());
                });
    }

    @Override
    public void onListClicked() {
        History history = History.single(new DtlMerchantsPath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            getView().prepareInfoWindow(pendingMapInfoEvent.height);
            pendingMapInfoEvent = null;
        }
    }

    public void onEvent(DtlMapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            getView().prepareInfoWindow(event.height);
        }
    }

    private void showPins(List<DtlMerchant> filtered) {
        getView().clearMap();
        Queryable.from(filtered).forEachR(dtlMerchant ->
                getView().addPin(dtlMerchant.getId(), new LatLng(dtlMerchant.getCoordinates().getLat(),
                        dtlMerchant.getCoordinates().getLng()), dtlMerchant.getMerchantType()));
        getView().renderPins();
    }

    @Override
    public void onMapLoaded() {
        mapReady = true;
        connectService();
        //
        getFirstCenterLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::centerIn);
        //
        showingLoadMerchantsButton()
                .compose(bindView())
                .subscribe(show -> getView().showButtonLoadMerchants(show));
        //
        MapObservableFactory.createMarkerClickObservable(getView().getMap())
                .compose(bindView())
                .subscribe(marker -> getView().markerClick(marker));
        //
        checkPendingMapInfo();
        gpsLocationDelegate.getLastKnownLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> tryHideMyLocationButton(false),
                        throwable -> tryHideMyLocationButton(true));
    }

    @Override
    public void applySearch(String query) {
        filterService.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }

    @Override
    public void locationChangeRequested() {
        History history = History.single(new DtlLocationChangePath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    @Override
    public void onMarkerClick(String merchantId) {
        merchantService.merchantByIdPipe()
                .createObservable(new DtlMerchantByIdAction(merchantId))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantByIdAction>()
                        .onSuccess(action -> getView().showPinInfo(action.getResult()))
                        .onFail(apiErrorPresenter::handleActionError));
    }

    @Override
    public void onLoadMerchantsClick(LatLng latLng) {
        DtlLocation mapSelectedLocation = ImmutableDtlManualLocation.builder()
                .locationSourceType(LocationSourceType.FROM_MAP)
                .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(latLng.latitude, latLng.longitude))
                .build();
        locationService.locationPipe().send(DtlLocationCommand.change(mapSelectedLocation));
        //
        android.location.Location location = new android.location.Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        merchantService.merchantsActionPipe().send(DtlMerchantsAction.load(location));
    }
}
