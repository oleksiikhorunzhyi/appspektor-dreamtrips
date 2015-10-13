package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DtlMapPresenter extends Presenter<DtlMapPresenter.View> {

    @Inject
    SnappyRepository db;

    private boolean mapReady;
    private DtlMapInfoReadyEvent pendingMapInfoEvent;

    List<DtlPlace> dtlPlaces = new ArrayList<>();

    public void onMapLoaded() {
        mapReady = true;
        dtlPlaces.clear();

        for (DtlPlaceType type : DtlPlaceType.values()) {
            dtlPlaces.addAll(db.getDtlPlaces(type));
        }

        showPins();
        checkPendingMapInfo();
    }

    public void onMarkerClick(int id) {
        showPlaceInfo(Queryable.from(dtlPlaces).firstOrDefault(item -> item.getId() == id));
    }

    private void showPlaceInfo(DtlPlace place) {
        if (place != null)
            view.showPlaceInfo(place);
    }

    private void showPins() {
        if (view != null) {
            view.clearMap();
            for (DtlPlace dtlPlace : dtlPlaces) {
                view.addPin(dtlPlace.getType(),
                        new LatLng(dtlPlace.getLocation().getLat(), dtlPlace.getLocation().getLng()),
                        String.valueOf(dtlPlace.getId()));
            }
        }
    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            view.prepareInfoWindow(pendingMapInfoEvent.getOffset());
            pendingMapInfoEvent = null;
        }
    }

    public void onEvent(DtlMapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            view.prepareInfoWindow(event.getOffset());
        }
    }

    public interface View extends Presenter.View {
        void addPin(DtlPlaceType placeType, LatLng latLng, String id);

        void clearMap();

        void showPlaceInfo(DtlPlace dtlPlace);

        void prepareInfoWindow(int offset);
    }
}
