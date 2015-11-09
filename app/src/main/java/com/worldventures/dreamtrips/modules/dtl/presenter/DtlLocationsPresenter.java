package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetNearbyDtlLocationQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View> {

    @Inject
    SnappyRepository db;

    @State
    ArrayList<DtlLocation> dtlLocations;
    @State
    Status status = Status.NEARBY;

    DtlLocation selectedLocation;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.startLoading();

        if (dtlLocations != null || searchLocations != null) {
            setItems();
            view.finishLoading();
            return;
        }

        searchLocations = new ArrayList<>();
        dtlLocations = new ArrayList<>();

        selectedLocation = db.getSelectedDtlLocation();
        //
        eventBus.post(new RequestLocationUpdateEvent());
    }

    public void onEvent(LocationObtainedEvent event) {
        if (event.getLocation() != null) loadNearbyCities(event.getLocation());
        else view.showSearch();
    }

    private void loadNearbyCities(Location currentLocation) {
        view.citiesLoadingStarted();
        doRequest(new GetNearbyDtlLocationQuery(currentLocation.getLatitude(),
                        currentLocation.getLongitude()),
                dtlLocations -> onNearbyLocationLoaded(dtlLocations, currentLocation));
    }

    private void onNearbyLocationLoaded(ArrayList<DtlLocation> dtlLocations, Location currentLocation) {
        this.dtlLocations = dtlLocations;
        view.finishLoading();
        setItems();

        if (dtlLocations.isEmpty()) view.showSearch();
        else if (selectedLocation == null) selectNearest(currentLocation);
    }

    private void selectNearest(Location currentLocation) {
        DtlLocation dtlLocation = Queryable.from(dtlLocations)
                .max(new DtlLocation.DtlNearestComparator(currentLocation));
        onLocationSelected(dtlLocation);
    }

    public void onLocationSelected(DtlLocation location) {
        db.saveSelectedDtlLocation(location);
        db.clearAllForKey(SnappyRepository.DTL_PLACES_PREFIX);
        view.showMerchants(new PlacesBundle(location));
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    private void setItems() {
        view.setItems(status == Status.NEARBY ? dtlLocations : searchLocations);
    }

    /////////////////////////////////////////////////////////////
    ////////// Search stuff
    /////////////////////////////////////////////////////////////

    public static final int SEARCH_SYMBOL_COUNT = 3;

    @State
    String caption;
    @State
    ArrayList<DtlLocation> searchLocations;

    private GetDtlLocationsQuery getDtlLocationsQuery;

    public void searchClosed() {
        status = Status.NEARBY;
        setItems();
    }

    public void searchOpened() {
        status = Status.SEARCH;
        setItems();
    }

    public void search(String caption) {
        if (view != null) {
            this.caption = caption;

            if (caption.length() < SEARCH_SYMBOL_COUNT) flushSearch();
            else if (caption.length() == SEARCH_SYMBOL_COUNT) apiSearch();
            else localSearch();
        }
    }

    private void flushSearch() {
        searchLocations.clear();
        setItems();
    }

    private void apiSearch() {
        view.citiesLoadingStarted();

        if (getDtlLocationsQuery != null) dreamSpiceManager.cancel(getDtlLocationsQuery);

        getDtlLocationsQuery = new GetDtlLocationsQuery(caption);
        doRequest(getDtlLocationsQuery, this::onSearchResultLoaded);
    }

    private void onSearchResultLoaded(ArrayList<DtlLocation> searchLocations) {
        this.searchLocations = searchLocations;
        localSearch();
    }

    private void localSearch() {
        if (searchLocations != null && !searchLocations.isEmpty())
            Observable.from(searchLocations)
                    .filter(dtlLocation ->
                            dtlLocation.getLongName().toLowerCase().contains(caption))
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setItems);
    }

    public interface View extends Presenter.View {

        void setItems(List<DtlLocation> dtlLocations);

        void startLoading();

        void finishLoading();

        void citiesLoadingStarted();

        void showMerchants(PlacesBundle bundle);

        void showSearch();
    }

    public enum Status {
        SEARCH, NEARBY
    }
}
