package com.worldventures.dreamtrips.modules.dtl_flow.parts.map;

import android.content.Context;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantStoreAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.store.DtlFilterMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.subjects.PublishSubject;

import static rx.Observable.just;

@SuppressWarnings("ConstantConditions")
public class DtlMapPresenterImpl extends DtlPresenterImpl<DtlMapScreen, ViewState.EMPTY> implements DtlMapPresenter {

    public static final int MAX_DISTANCE = 50;

    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    SnappyRepository db;
    @Inject
    protected Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    Janet janet;
    @Inject
    DtlMerchantStore merchantStore;
    @Inject
    DtlFilterMerchantStore filterStore;
    @Inject
    DtlFilterMerchantStore filteredMerchantStore;
    //
    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    private final PublishSubject<List<DtlMerchant>> merchantsStream = PublishSubject.create();
    private final WriteActionPipe<DtlFilterMerchantStoreAction> filteredMerchantStoreActionPipe;
    private final WriteActionPipe<DtlMerchantStoreAction> merchantStoreActionPipe;

    public DtlMapPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
        filteredMerchantStoreActionPipe = janet.createPipe(DtlFilterMerchantStoreAction.class);
        merchantStoreActionPipe = janet.createPipe(DtlMerchantStoreAction.class);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().hideDinings(db.getLastSelectedOffersOnlyToggle());
        //
        bindFilteredStream();
        bindLocationStream();
        bindFilterState();
    }

    protected void connectMerchants() {
        merchantStore.merchantsActionPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlMerchantsAction>()
                        .onStart(action -> getView().showProgress(true)));
        //
        filteredMerchantStore.filteredMerchantsChangesPipe()
                .observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlFilterMerchantsAction>()
                        .onFail((action, throwable) -> getView().showProgress(false))
                        .onSuccess(action -> onMerchantsLoaded(action.getResult())));
        //
        filterStore.getFilterDataState()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    protected void bindFilteredStream() {
        final Observable<List<DtlMerchant>> merchantsStream =
                Observable.combineLatest(this.merchantsStream, prepareFilterToogle(),
                        this::filterMerchantsByType);
        merchantsStream.compose(bindView()).subscribe(this::showPins);
    }

    private List<DtlMerchant> filterMerchantsByType(List<DtlMerchant> merchants, boolean hideDinings) {
        return Observable.from(merchants)
                .filter(merchant -> !(hideDinings && merchant.getMerchantType() == DtlMerchantType.DINING))
                .toList().toBlocking().firstOrDefault(Collections.emptyList());
    }

    private void bindLocationStream() {
        Observable.combineLatest(
                dtlLocationManager.getSelectedLocation().map(DtlLocationCommand::getResult),
                filteredMerchantStore.getFilterDataState().map(DtlFilterData::getSearchQuery),
                Pair::new
        ).compose(bindViewIoToMainComposer())
                .take(1)
                .subscribe(pair -> {
                    getView().updateToolbarTitle(pair.first, pair.second);
                });
    }

    private void bindFilterState() {
        filterStore.observeStateChange()
                .compose(bindViewIoToMainComposer())
                .subscribe(dtlFilterData ->
                        getView().setFilterButtonState(!dtlFilterData.isDefault()));
    }

    private Observable<Boolean> prepareFilterToogle() {
        return getView().getToggleObservable()
                .startWith(db.getLastSelectedOffersOnlyToggle())
                .doOnNext(checked -> db.saveLastSelectedOffersOnlyToogle(checked));
    }

    protected void tryHideMyLocationButton(boolean hide) {
        getView().tryHideMyLocationButton(hide);
    }

    protected Observable<Location> getFirstCenterLocation() {
        return dtlLocationManager.getSelectedLocation()
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
                .doOnNext(position -> db.saveLastMapCameraPosition(new Location(position.target.latitude, position.target.longitude)))
                .flatMap(position -> {
                    if (position.zoom < MapViewUtils.DEFAULT_ZOOM) {
                        return just(true);
                    }
                    return dtlLocationManager.getSelectedLocation()
                            .map(command -> !DtlLocationHelper.checkLocation(MAX_DISTANCE,
                                    command.getResult().getCoordinates().asLatLng(),
                                    position.target, DistanceType.MILES));
                })
                .compose(bindView());
    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        this.merchantsStream.onNext(dtlMerchants);
        //
        getView().showProgress(false);
        getView().showButtonLoadMerchants(false);
        //
        dtlLocationManager.getSelectedLocation()
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> {
                    if (dtlMerchants.isEmpty() && location.getLocationSourceType() == LocationSourceType.FROM_MAP)
                        getView().informUser(R.string.dtl_no_merchants_caption);
                    //
                    if (location.getLocationSourceType() == LocationSourceType.FROM_MAP && getView().getMap().getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM)
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
        connectMerchants();
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
                .compose(bindView())
                .subscribe(location -> tryHideMyLocationButton(false),
                        throwable -> tryHideMyLocationButton(true));
    }

    @Override
    public void applySearch(String query) {
        filteredMerchantStoreActionPipe.send(DtlFilterMerchantStoreAction.applySearch(query));
    }

    @Override
    public void locationChangeRequested() {
        History history = History.single(new DtlLocationChangePath());
        Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
    }

    @Override
    public void onMarkerClick(String merchantId) {
        getView().showPinInfo(merchantId);
    }

    @Override
    public void onLoadMerchantsClick(LatLng latLng) {
        DtlLocation mapSelectedLocation = ImmutableDtlManualLocation.builder()
                .locationSourceType(LocationSourceType.FROM_MAP)
                .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(latLng.latitude, latLng.longitude))
                .build();
        dtlLocationManager.persistLocation(mapSelectedLocation);
        //
        android.location.Location location = new android.location.Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        merchantStoreActionPipe.send(DtlMerchantStoreAction.load(location));
    }
}
