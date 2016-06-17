package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
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
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
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
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

import static rx.Observable.just;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY>
        implements DtlMapPresenter {

    public static final int MAX_DISTANCE = 50;

    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    protected Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    Janet janet;
    @Inject
    DtlMerchantInteractor merchantInteractor;
    @Inject
    DtlFilterMerchantInteractor filterInteractor;
    @Inject
    DtlLocationInteractor locationInteractor;
    @Inject
    DtlTransactionInteractor pipesInteractor;
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
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::isOffersOnly)
                .subscribe(getView()::toggleDiningFilterSwitch);
        getView().getToggleObservable()
                .subscribe(offersOnly -> filterInteractor.filterDataPipe()
                        .send(DtlFilterDataAction.applyOffersOnly(offersOnly)));
        //
        updateToolbarTitles();
        bindToolbarTitleUpdates();
        updateFilterButtonState();
    }

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) getView().prepareMap();
    }

    protected void connectInteractors() {
        // TODO :: temporary solutions
        locationInteractor.locationPipe().observeSuccess()
                .map(DtlLocationCommand::getResult)
                .filter(command -> tabletAnalytic.isTabletLandscape())
                .distinctUntilChanged(loc -> loc.getCoordinates().asLatLng())
                .map(DtlLocation::getCoordinates)
                .compose(bindViewIoToMainComposer())
                .subscribe(loc -> getView().animateTo(loc.asLatLng(), 0));
        merchantInteractor.merchantsActionPipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .compose(new ActionStateToActionTransformer<>())
                .filter(dtlMerchantsAction -> dtlMerchantsAction.getResult().isEmpty())
                .subscribe(s -> getView().informUser(R.string.dtl_no_merchants_caption),
                        throwable -> {});
        merchantInteractor.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress(true)));
        filterInteractor.filterMerchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> {
                            getView().showProgress(false);
                            apiErrorPresenter.handleActionError(action, throwable);
                        })
                        .onSuccess(action -> onMerchantsLoaded(action.getResult())));
        filterInteractor.filterDataPipe().observeSuccess()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    private void updateToolbarTitles() {
        locationInteractor.locationPipe().observeSuccessWithReplay()
                .first()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .compose(bindViewIoToMainComposer())
                .map(DtlFilterDataAction::getResult)
                .map(DtlFilterData::getSearchQuery)
                .subscribe(getView()::updateToolbarSearchCaption);
    }

    private void bindToolbarTitleUpdates() {
        locationInteractor.locationPipe().observeSuccess()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(getView()::updateToolbarLocationTitle);
    }

    private void updateFilterButtonState() {
        filterInteractor.filterDataPipe().observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    protected void tryHideMyLocationButton(boolean hide) {
        getView().tryHideMyLocationButton(hide);
    }

    protected Observable<Location> getFirstCenterLocation() {
        return locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                .map(command -> {
                    Location lastPosition = db.getLastMapCameraPosition();
                    boolean validLastPosition = lastPosition != null
                            && lastPosition.getLat() != 0
                            && lastPosition.getLng() != 0;
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
                    return locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
                            .compose(bindViewIoToMainComposer())
                            .map(command -> !DtlLocationHelper.checkLocation(MAX_DISTANCE,
                                    command.getResult().getCoordinates().asLatLng(),
                                    position.target, DistanceType.MILES));
                });
    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        getView().showProgress(false);
        getView().showButtonLoadMerchants(false);
        showPins(dtlMerchants);
        //
        locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last())
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
        //
        connectInteractors();
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
        filterInteractor.filterDataPipe().send(DtlFilterDataAction.applySearch(query));
    }

    @Override
    public void locationChangeRequested() {
        History history = History.single(new DtlLocationChangePath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    @Override
    public void onMarkerClick(String merchantId) {
        merchantInteractor.merchantByIdPipe()
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
        locationInteractor.locationPipe().send(DtlLocationCommand.change(mapSelectedLocation));
        //
        android.location.Location location = new android.location.Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        merchantInteractor.merchantsActionPipe().send(DtlMerchantsAction.load(location));
    }
}
