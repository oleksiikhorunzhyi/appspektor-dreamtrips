package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.DtlModule;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlFilterEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlMapPresenter extends Presenter<DtlMapPresenter.View> {

    @Inject
    SnappyRepository db;

    @State
    DtlFilterData dtlFilterData;

    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    DtlLocation location;
    List<DtlPlace> dtlPlaces = new ArrayList<>();

    public DtlMapPresenter(PlacesBundle bundle) {
        location = bundle.getLocation();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(location);
        if (dtlFilterData == null) {
            dtlFilterData = new DtlFilterData();
        }
    }

    public void onMapLoaded() {
        mapReady = true;
        dtlPlaces.clear();

        view.centerIn(location);

        for (DtlPlaceType type : DtlPlaceType.values()) {
            dtlPlaces.addAll(db.getDtlPlaces(type));
        }

        showPins();
        checkPendingMapInfo();
    }

    public void onMarkerClick(String merchantId) {
        showPlaceInfo(Queryable.from(dtlPlaces).firstOrDefault(item -> item.getMerchantId().equals(merchantId)));
    }

    private void showPlaceInfo(DtlPlace place) {
        view.showPlaceInfo(place);
    }

    private void showPins() {
        if (view != null) {
            view.clearMap();
            List<DtlPlace> filtered = Queryable.from(dtlPlaces).filter(dtlPlace ->
                    dtlPlace.applyFilter(dtlFilterData, new LatLng(DtlModule.LAT, DtlModule.LNG))).toList();

            for (DtlPlace dtlPlace : filtered) {
                view.addPin(dtlPlace.getMerchantId(), new LatLng(dtlPlace.getCoordinates().getLat(),
                        dtlPlace.getCoordinates().getLng()), dtlPlace.getPartnerStatus());
            }
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

    public void onEventMainThread(DtlFilterEvent event) {
        dtlFilterData = event.getDtlFilterData();
        if (mapReady)
            showPins();
    }

    public interface View extends Presenter.View {
        void addPin(String id, LatLng latLng, DtlPlaceType type);

        void clearMap();

        void showPlaceInfo(DtlPlace dtlPlace);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);
    }
}
