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
import com.worldventures.dreamtrips.modules.dtl.delegate.DtlFilterDelegate;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.TogglePlaceSelectionEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class DtlPlacesListPresenter extends Presenter<DtlPlacesListPresenter.View> implements DtlFilterDelegate.FilterListener {

    @Inject
    SnappyRepository db;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlFilterDelegate dtlFilterDelegate;

    protected DtlMerchantType placeType;

    private List<DtlMerchant> DtlMerchants;

    private DtlLocation dtlLocation;

    public DtlPlacesListPresenter(DtlMerchantType placeType) {
        this.placeType = placeType;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        DtlMerchants = db.getDtlPlaces(placeType);
        dtlLocation = db.getSelectedDtlLocation();

        if (DtlMerchants.isEmpty()) view.showProgress();

        dtlFilterDelegate.addListener(this);

        performFiltering();

        if (placeType == DtlMerchantType.OFFER) view.setComingSoon();
    }

    @Override
    public void dropView() {
        dtlFilterDelegate.removeListener(this);
        super.dropView();
    }

    public void onEventMainThread(PlacesUpdatedEvent event) {
        if (!event.getType().equals(placeType)) return;
        //
        DtlMerchants = db.getDtlPlaces(placeType);
        performFiltering();
    }

    public void onEventMainThread(TogglePlaceSelectionEvent event) {
        if (DtlMerchants.contains(event.getDtlMerchant())) view.toggleSelection(event.getDtlMerchant());
    }

    @Override
    public void onFilter() {
        performFiltering();
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
        ).subscribe(view::setItems, this::onError);
    }

    private Observable<List<DtlMerchant>> filter(Location location, String query) {
        LatLng currentLocation = LocationHelper.checkLocation(DtlFilterData.MAX_DISTANCE,
                new LatLng(location.getLatitude(), location.getLongitude()),
                dtlLocation.getCoordinates().asLatLng(),
                DtlFilterData.DistanceType.MILES)
                ? new LatLng(location.getLatitude(), location.getLongitude())
                : dtlLocation.getCoordinates().asLatLng();

        List<DtlMerchant> places = Queryable.from(DtlMerchants)
                .filter(dtlPlace ->
                        dtlPlace.applyFilter(dtlFilterDelegate.getDtlFilterData(),
                                currentLocation))
                .filter(dtlPlace -> dtlPlace.containsQuery(query)).toList();

        for (DtlMerchant DtlMerchant : places) {
            DtlMerchant.calculateDistance(currentLocation);
        }

        Collections.sort(places, DtlMerchant.DISTANCE_COMPARATOR);

        if (!query.isEmpty()) TrackingHelper.dtlMerchantSearch(query, places.size());

        return Observable.from(places).toList();
    }


    public void onEventMainThread(PlacesUpdateFinished event) {
        view.hideProgress();
    }

    public void onEventMainThread(DtlSearchPlaceRequestEvent event) {
        performFiltering(event.getSearchQuery());
    }

    private void onError(Throwable e) {
        Timber.e(e, "Something went wrong while filtering");
    }

    public interface View extends RxView {

        void setItems(List<DtlMerchant> places);

        void showProgress();

        void hideProgress();

        void toggleSelection(DtlMerchant DtlMerchant);

        void setComingSoon();
    }
}