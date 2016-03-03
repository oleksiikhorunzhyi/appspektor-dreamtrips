package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import java.util.List;

import javax.inject.Inject;

public class DtlMapPresenter extends JobPresenter<DtlMapPresenter.View> {

    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(dtlLocationManager.getCachedSelectedLocation());
        //
        bindJobPersistantCached(dtlMerchantManager.getMerchantsExecutor)
                .onSuccess(this::onMerchantsLoaded);
    }

    public void onMapLoaded() {
        mapReady = true;
        //
        view.centerIn(dtlLocationManager.getCachedSelectedLocation());
        checkPendingMapInfo();
    }

    protected void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        showPins(dtlMerchants);
    }

    public void onMarkerClick(String merchantId) {
        view.showMerchantInfo(merchantId);
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

    public interface View extends RxView {
        void addPin(String id, LatLng latLng, DtlMerchantType type);

        void clearMap();

        void showMerchantInfo(String merchantId);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);

        void renderPins();
    }
}
