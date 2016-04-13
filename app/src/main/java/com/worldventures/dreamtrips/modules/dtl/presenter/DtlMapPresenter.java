package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static rx.Observable.just;

public class DtlMapPresenter extends JobPresenter<DtlMapPresenter.View> {

    public static final int MAX_DISTANCE = 50;

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    @Inject
    LocationDelegate gpsLocationDelegate;
    @Inject
    SnappyRepository db;
    //
    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    private final PublishSubject<List<DtlMerchant>> merchantsStream = PublishSubject.create();
    private BehaviorSubject<Boolean> toggleStream;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        toggleStream = BehaviorSubject.create(db.getLastSelectedOffersOnlyToggle());
        //
        bindJobObservable(dtlMerchantManager.connectMerchantsWithCache())
                .onProgress(() -> view.showProgress(true))
                .onError(throwable -> view.showProgress(false))
                .onSuccess(this::onMerchantsLoaded);
        bindFilteredStream();
        bindLocationStream();
    }

    protected void bindFilteredStream() {
        final Observable<List<DtlMerchant>> merchantsStream = Observable.combineLatest(this.merchantsStream, prepareFilterToogle(), (dtlMerchants, hideDinings) ->
                Observable.from(dtlMerchants)
                        .filter(merchant -> !(hideDinings && merchant.getMerchantType() == DtlMerchantType.DINING))
                        .toList().toBlocking().firstOrDefault(Collections.emptyList()));
        view.bind(merchantsStream).subscribe(this::showPins);
    }

    private void bindLocationStream() {
        view.bind(dtlLocationManager.getSelectedLocation())
                .map(DtlLocationCommand::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(location -> view.updateToolbarTitle(location));
    }

    private Observable<Boolean> prepareFilterToogle() {
        return toggleStream
                .doOnSubscribe(() -> view.hideDinings(db.getLastSelectedOffersOnlyToggle()))// set initial value before emitting switching
                .doOnNext(st -> db.saveLastSelectedOffersOnlyToogle(st));
    }

    public void onMapLoaded() {
        mapReady = true;
        //
        getFirstCenterLocation()
                .compose(bindViewIoToMainComposer())
                .subscribe(view::centerIn);
        view.bind(showingLoadMerchantsButton())
                .subscribe(show -> view.showButtonLoadMerchants(show));
        view.bind(MapObservableFactory.createMarkerClickObservable(view.getMap())).subscribe(marker -> view.markerClick(marker));
        //
        checkPendingMapInfo();
        view.bind(gpsLocationDelegate.getLastKnownLocation())
                .compose(new IoToMainComposer<>())
                .subscribe(location -> tryHideMyLocationButton(false),
                        throwable -> tryHideMyLocationButton(true));
    }

    protected void tryHideMyLocationButton(boolean hide) {
        if (view != null) view.tryHideMyLocationButton(hide);
    }

    protected Observable<Location> getFirstCenterLocation() {
        return dtlLocationManager.getSelectedLocation()
                .take(1)
                .compose(bindViewIoToMainComposer())
                .map(command -> {
                    Location lastPosition = db.getLastMapCameraPosition();
                    DtlLocation lastSelectedLocation = command.getResult();
                    return lastPosition != null ? lastPosition : (command.isResultDefined() ?
                            lastSelectedLocation.getCoordinates() : new Location(0d, 0d));
                });
    }

    protected Observable<Boolean> showingLoadMerchantsButton() {
        return view.bind(MapObservableFactory.createCameraChangeObservable(view.getMap())
                .doOnNext(position -> view.cameraPositionChange(position))
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
                .observeOn(AndroidSchedulers.mainThread()));
    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        this.merchantsStream.onNext(dtlMerchants);
        //
        view.showProgress(false);
        view.showButtonLoadMerchants(false);
        //
        dtlLocationManager.getSelectedLocation()
                .filter(DtlLocationCommand::isResultDefined)
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> {
                    if (dtlMerchants.isEmpty() &&
                            command.getResult().getLocationSourceType() == LocationSourceType.FROM_MAP)
                        view.informUser(R.string.dtl_no_merchants_caption);
                    //
                    if (command.getResult().getLocationSourceType() == LocationSourceType.FROM_MAP &&
                            view.getMap().getCameraPosition().zoom < MapViewUtils.DEFAULT_ZOOM)
                        view.zoom(MapViewUtils.DEFAULT_ZOOM);
                    //
                    if (command.getResult().getLocationSourceType() != LocationSourceType.NEAR_ME)
                        view.addLocationMarker(command.getResult().getCoordinates().asLatLng());
                });

    }

    public void onMarkerClick(String merchantId) {
        view.showMerchantInfo(merchantId);
    }

    public void onCheckHideDinings(boolean checked) {
        toggleStream.onNext(checked);
    }

    public void applySearch(String query) {
        dtlMerchantManager.applySearch(query);
    }

    private void showPins(List<DtlMerchant> filtered) {
        if (view == null) return;
        //
        view.clearMap();
        Queryable.from(filtered).forEachR(dtlMerchant ->
                view.addPin(dtlMerchant.getId(), new LatLng(dtlMerchant.getCoordinates().getLat(),
                        dtlMerchant.getCoordinates().getLng()), dtlMerchant.getMerchantType()));
        view.renderPins();

    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            view.prepareInfoWindow(pendingMapInfoEvent.height);
            pendingMapInfoEvent = null;
        }
    }

    public void onEvent(DtlMapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            view.prepareInfoWindow(event.height);
        }
    }

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
        dtlMerchantManager.loadMerchants(location);
    }

    public interface View extends RxView {
        void showProgress(boolean show);

        void addLocationMarker(LatLng location);

        void addPin(String id, LatLng latLng, DtlMerchantType type);

        void clearMap();

        void showMerchantInfo(String merchantId);

        void prepareInfoWindow(int height);

        void centerIn(Location location);

        void renderPins();

        void hideDinings(boolean hide);

        GoogleMap getMap();

        void cameraPositionChange(CameraPosition cameraPosition);

        void markerClick(Marker marker);

        void showButtonLoadMerchants(boolean show);

        void zoom(float zoom);

        void updateToolbarTitle(@Nullable DtlLocation dtlLocation);

        void tryHideMyLocationButton(boolean hide);
    }
}
