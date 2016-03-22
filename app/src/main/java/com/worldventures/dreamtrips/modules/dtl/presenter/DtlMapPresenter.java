package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.map.view.MapViewUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class DtlMapPresenter extends JobPresenter<DtlMapPresenter.View> {

    public static final int MAX_DISTANCE = 50;

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
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
        view.initToolbar(dtlLocationManager.getCachedSelectedLocation());
        toggleStream = BehaviorSubject.create(db.getLastSelectedOffersOnlyToogle());
        //
        bindJobPersistantCached(dtlMerchantManager.getMerchantsExecutor).onSuccess(this::onMerchantsLoaded);
        bindFilteredStream();
    }

    protected void bindFilteredStream() {
        final Observable<List<DtlMerchant>> merchantsStream = Observable.combineLatest(this.merchantsStream, prepareFilterToogle(), (dtlMerchants, hideDinings) ->
                Observable.from(dtlMerchants)
                        .filter(merchant -> !(hideDinings && merchant.getMerchantType() == DtlMerchantType.DINING))
                        .toList().toBlocking().firstOrDefault(Collections.emptyList()));
        view.bind(merchantsStream).subscribe(this::showPins);
    }

    private Observable<Boolean> prepareFilterToogle() {
        return toggleStream
                .doOnSubscribe(() -> view.hideDinings(db.getLastSelectedOffersOnlyToogle()))// set initial value before emitting switching
                .doOnNext(st -> db.saveLastSelectedOffersOnlyToogle(st));
    }

    public void onMapLoaded() {
        mapReady = true;
        //
        view.bind(subscribeToCameraChange()).subscribe(show -> view.showButtonLoadMerchants(show));
        view.bind(MapObservableFactory.createMarkerClickObservable(view.getMap())).subscribe(marker -> view.markerClick(marker));
        view.centerIn(dtlLocationManager.getCachedSelectedLocation());
        checkPendingMapInfo();
    }

    protected Observable<Boolean> subscribeToCameraChange() {
        return MapObservableFactory.createCameraChangeObservable(view.getMap())
                .doOnNext(position -> view.cameraPositionChange(position))
                .map(position -> position.zoom < MapViewUtils.DEFAULT_ZOOM ||
                        !DtlLocationHelper.checkLocation(MAX_DISTANCE, dtlLocationManager.getCachedSelectedLocation().asLatLng(), position.target, DistanceType.MILES));

    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        this.merchantsStream.onNext(dtlMerchants);
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
        if (view != null) {
            view.clearMap();
            Queryable.from(filtered).forEachR(dtlMerchant ->
                    view.addPin(dtlMerchant.getId(), new LatLng(dtlMerchant.getCoordinates().getLat(),
                            dtlMerchant.getCoordinates().getLng()), dtlMerchant.getMerchantType()));
            view.renderPins();
        }
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

    public void onLoadMerchantsClick(LatLng location){
        //TODO dtlMerchantManager.loadMerchants();
        if(view.getMap().getCameraPosition().zoom < 10f) view.zoom(10f);
    }

    public interface View extends RxView {
        void addPin(String id, LatLng latLng, DtlMerchantType type);

        void clearMap();

        void showMerchantInfo(String merchantId);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);

        void renderPins();

        void hideDinings(boolean hide);

        GoogleMap getMap();

        void cameraPositionChange(CameraPosition cameraPosition);

        void markerClick(Marker marker);

        void showButtonLoadMerchants(boolean show);

        void zoom(float zoom);
    }
}
