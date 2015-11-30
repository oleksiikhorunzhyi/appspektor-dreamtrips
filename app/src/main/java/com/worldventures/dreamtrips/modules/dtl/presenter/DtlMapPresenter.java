package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesMapBundle;
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.DtlMapInfoReadyEvent;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

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

    List<DtlPlace> dtlPlaces = new ArrayList<>();

    public DtlMapPresenter(PlacesMapBundle bundle) {
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

    public void onMapLoaded() {
        mapReady = true;
        dtlPlaces.clear();

        view.centerIn(dtlLocation);

        for (DtlPlaceType type : DtlPlaceType.values()) {
            dtlPlaces.addAll(db.getDtlPlaces(type));
        }

        performFiltering();
        checkPendingMapInfo();
    }

    public void onMarkerClick(String merchantId) {
        showPlaceInfo(Queryable.from(dtlPlaces).firstOrDefault(item -> item.getMerchantId().equals(merchantId)));
    }

    private void showPlaceInfo(DtlPlace place) {
        view.showPlaceInfo(place);
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

    private Observable<List<DtlPlace>> filter(Location location, String query) {
        LatLng currentLocation = new LatLng(location.getLatitude(),
                location.getLongitude());

        List<DtlPlace> places = Queryable.from(dtlPlaces)
                .filter(dtlPlace ->
                        dtlPlace.applyFilter(dtlFilterDelegate.getDtlFilterData(),
                                currentLocation))
                .filter(dtlPlace -> dtlPlace.containsQuery(query))
                .toList();

        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, places.size());

        return Observable.from(places).toList();
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    private void showPins(List<DtlPlace> filtered) {
        if (view != null) {
            view.clearMap();
            Queryable.from(filtered).forEachR(dtlPlace ->
                    view.addPin(dtlPlace.getMerchantId(), new LatLng(dtlPlace.getCoordinates().getLat(),
                            dtlPlace.getCoordinates().getLng()), dtlPlace.getPlaceType()));
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
        void addPin(String id, LatLng latLng, DtlPlaceType type);

        void clearMap();

        void showPlaceInfo(DtlPlace dtlPlace);

        void prepareInfoWindow(int height);

        void initToolbar(DtlLocation location);

        void centerIn(DtlLocation location);

        void renderPins();
    }
}
