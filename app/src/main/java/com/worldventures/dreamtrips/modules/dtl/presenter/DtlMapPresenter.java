package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsMapBundle;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchMerchantRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class DtlMapPresenter extends Presenter<DtlMapPresenter.View> implements DtlFilterDelegate.FilterListener {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;

    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    private DtlLocation dtlLocation;

    List<DtlMerchant> dtlMerchants = new ArrayList<>();

    public DtlMapPresenter(MerchantsMapBundle bundle) {
        dtlLocation = bundle.getLocation();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(dtlLocation);
        dtlFilterDelegate.addListener(this);
    }

    @Override
    public void dropView() {
        dtlFilterDelegate.removeListener(this);
        super.dropView();
    }

    public void onEventMainThread(MerchantUpdatedEvent event) {
        setMerchants();
    }

    public void onMapLoaded() {
        mapReady = true;

        view.centerIn(dtlLocation);
        setMerchants();
    }

    private void setMerchants() {
        dtlMerchants.clear();
        for (DtlMerchantType type : DtlMerchantType.values()) {
            dtlMerchants.addAll(db.getDtlMerchants(type));
        }

        performFiltering();
        checkPendingMapInfo();
    }

    public void onMarkerClick(String merchantId) {
        showMerchantInfo(Queryable.from(dtlMerchants).firstOrDefault(item -> item.getId().equals(merchantId)));
    }

    private void showMerchantInfo(DtlMerchant merchant) {
        view.showMerchantInfo(merchant);
    }

    private void performFiltering() {
        performFiltering("");
    }

    private void performFiltering(String query) {
        view.bind(locationDelegate
                        .getLastKnownLocation()
                        .onErrorResumeNext(Observable.just(dtlLocation.asAndroidLocation()))
                        .flatMap(location -> filter(location, query))
                        .compose(new IoToMainComposer<>())
        ).subscribe(this::showPins, this::onError);
    }

    private Observable<List<DtlMerchant>> filter(Location location, String query) {
        LatLng currentLocation = LocationHelper.checkLocation(DtlFilterData.MAX_DISTANCE,
                new LatLng(location.getLatitude(), location.getLongitude()),
                dtlLocation.getCoordinates().asLatLng(),
                DtlFilterData.DistanceType.MILES)
                ? new LatLng(location.getLatitude(), location.getLongitude())
                : dtlLocation.getCoordinates().asLatLng();

        List<DtlMerchant> merchants = Queryable.from(dtlMerchants)
                .filter(dtlMerchant ->
                        dtlMerchant.applyFilter(dtlFilterDelegate.getDtlFilterData(),
                                currentLocation))
                .filter(dtlMerchant -> dtlMerchant.containsQuery(query))
                .toList();

        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, merchants.size());

        return Observable.from(merchants).toList();
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
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

    public void onEventMainThread(DtlSearchMerchantRequestEvent event) {
        performFiltering(event.getSearchQuery());
    }

    public interface View extends RxView {
        void addPin(String id, LatLng latLng, DtlMerchantType type);

        void clearMap();

        void showMerchantInfo(DtlMerchant DtlMerchant);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);

        void renderPins();
    }
}
