package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.List;

public class DtlMapPresenter extends DtlMerchantsPresenter<DtlMapPresenter.View> {

    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    public DtlMapPresenter(DtlMapBundle bundle) {
        dtlLocation = bundle.getLocation();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(dtlLocation);
    }

    public void onMapLoaded() {
        mapReady = true;
        //
        view.centerIn(dtlLocation);
        performFiltering();
        checkPendingMapInfo();
    }

    @Override
    protected void merchantsPrepared(List<DtlMerchant> dtlMerchants) {
        showPins(dtlMerchants);
    }

    public void onMarkerClick(String merchantId) {
        view.showPlaceInfo(merchantId);
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
            super.onFilter();
    }

    public void onEvent(DtlMapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            view.prepareInfoWindow(event.height);
        }
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
