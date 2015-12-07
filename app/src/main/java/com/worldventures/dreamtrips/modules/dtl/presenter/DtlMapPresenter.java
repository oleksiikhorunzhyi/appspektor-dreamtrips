package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class DtlMapPresenter extends Presenter<DtlMapPresenter.View> implements
        DtlFilterDelegate.FilterListener, DtlMerchantStore.MerchantUpdatedListener {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;
    @Inject
    DtlMerchantStore dtlMerchantStore;

    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    private DtlLocation dtlLocation;

    public DtlMapPresenter(DtlMapBundle bundle) {
        dtlLocation = bundle.getLocation();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchantStore.attachListener(this);
        dtlFilterDelegate.addListener(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(dtlLocation);
    }

    @Override
    public void dropView() {
        dtlMerchantStore.detachListener(this);
        dtlFilterDelegate.removeListener(this);
        super.dropView();
    }

    public void onMapLoaded() {
        mapReady = true;
        //
        view.centerIn(dtlLocation);
        setMerchants();
    }

    @Override
    public void onMerchantsUploaded() {
        setMerchants();
    }

    @Override
    public void onMerchantsFailed(SpiceException exception) {
        //
    }

    private void setMerchants() {
        performFiltering();
        checkPendingMapInfo();
    }

    public void onMarkerClick(String merchantId) {
        view.showPlaceInfo(merchantId);
    }

    private void performFiltering() {
        performFiltering("");
    }

    private void performFiltering(String query) {
        view.bind(locationDelegate
                        .getLastKnownLocation()
                        .onErrorResumeNext(Observable.just(dtlLocation.asAndroidLocation()))
                        .flatMap(location -> mapToMerchantList(location, query))
                        .doOnNext(merchants -> track(merchants, query))
                        .compose(new IoToMainComposer<>())
        ).subscribe(this::showPins, this::onError);
    }

    private Observable<List<DtlMerchant>> mapToMerchantList(Location location, String query) {
        return Observable.from(
                Queryable.from(dtlMerchantStore.getMerchants())
                        .filter(DtlMerchantsPredicate.Builder.create()
                                .withDtlFilterData(dtlFilterDelegate.getDtlFilterData())
                                .withLatLng(LocationHelper.getAcceptedLocation(location, dtlLocation))
                                .withQuery(query)
                                .build())
                        .toList()
        ).toList();
    }

    private void track(List<DtlMerchant> merchants, String query) {
        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, merchants.size());
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    private void showPins(List<DtlMerchant> filtered) {
        if (view != null) {
            view.clearMap();
            Queryable.from(filtered).forEachR(dtlPlace ->
                    view.addPin(dtlPlace.getId(), new LatLng(dtlPlace.getCoordinates().getLat(),
                            dtlPlace.getCoordinates().getLng()), dtlPlace.getMerchantType()));
            view.renderPins();
        }
    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            view.prepareInfoWindow(pendingMapInfoEvent.height);
            pendingMapInfoEvent = null;
        }
    }

    @Override
    public void onFilter() {
        if (mapReady)
            performFiltering();
    }

    public void onEvent(DtlMapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            view.prepareInfoWindow(event.height);
        }
    }

    public void onEventMainThread(DtlSearchPlaceRequestEvent event) {
        performFiltering(event.getSearchQuery());
    }

    public interface View extends RxView {
        void addPin(String id, LatLng latLng, DtlMerchantType type);

        void clearMap();

        void showPlaceInfo(String merchantId);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);

        void renderPins();
    }
}
