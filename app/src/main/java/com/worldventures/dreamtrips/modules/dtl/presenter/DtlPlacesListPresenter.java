package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.DtlFilterEvent;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class DtlPlacesListPresenter extends Presenter<DtlPlacesListPresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;

    protected DtlPlaceType placeType;

    private List<DtlPlace> dtlPlaces;

    @State
    DtlFilterData dtlFilterData;

    private LatLng currentLocation;

    public DtlPlacesListPresenter(DtlPlaceType placeType) {
        this.placeType = placeType;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlPlaces = db.getDtlPlaces(placeType);

        if (dtlPlaces.isEmpty()) view.showProgress();

        if (dtlFilterData == null) {
            dtlFilterData = new DtlFilterData();
        }

        locationDelegate.getLastKnownLocation(location ->
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude()),
                this::performFiltering);
    }

    public void onEventMainThread(PlacesUpdatedEvent event) {
        if (!event.getType().equals(placeType)) return;
        //
        dtlPlaces = db.getDtlPlaces(placeType);
        performFiltering();
    }

    public void onEventMainThread(DtlFilterEvent event) {
        dtlFilterData = event.getDtlFilterData();
        performFiltering();
    }

    private void performFiltering() {
        view.setItems(Queryable.from(dtlPlaces).filter(dtlPlace ->
                dtlPlace.applyFilter(dtlFilterData, currentLocation)).toList());
    }

    public void onEventMainThread(PlacesUpdateFinished event) {
        view.hideProgress();
    }

    public interface View extends Presenter.View {

        void setItems(List<DtlPlace> places);

        void showProgress();

        void hideProgress();
    }
}
