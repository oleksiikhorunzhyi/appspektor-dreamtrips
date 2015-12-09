package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.location.Location;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.api.location.GetDtlLocationsQuery;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationRepository;
import com.worldventures.dreamtrips.modules.dtl.event.LocationObtainedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.RequestLocationUpdateEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;
import timber.log.Timber;

public class DtlLocationsPresenter extends Presenter<DtlLocationsPresenter.View>
implements DtlLocationRepository.LocationsLoadedListener {

    @Inject
    DtlLocationRepository dtlLocationRepository;
    //
    @State
    ArrayList<DtlLocation> dtlLocations;
    @State
    Status status = Status.NEARBY;
    //
    private Location userGpsLocation;

    @Override
    public void onInjected() {
        super.onInjected();
        dtlLocationRepository.setRequestingPresenter(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        dtlLocationRepository.attachListener(this);
        //
        if (dtlLocations != null || searchLocations != null) {
            setItems();
            return;
        }
        //
        searchLocations = new ArrayList<>();
        dtlLocations = new ArrayList<>();
        //
        eventBus.post(new RequestLocationUpdateEvent());
        //
        view.startLoading();
    }

    public void onEvent(LocationObtainedEvent event) {
        if (event.getLocation() != null) {
            userGpsLocation = event.getLocation();
            view.citiesLoadingStarted();
            dtlLocationRepository.loadNearbyLocations(userGpsLocation);
        } else {
            view.finishLoading();
            view.showSearch();
        }
    }

    @Override
    public void onLocationsLoaded(List<DtlLocation> locations) {
        this.dtlLocations = dtlLocations;
        view.finishLoading();
        setItems();
        //
        if (dtlLocations.isEmpty()) view.showSearch();
        else if (dtlLocationRepository.getSelectedLocation() == null) selectNearest(userGpsLocation);
    }

    private void selectNearest(Location currentLocation) {
        DtlLocation dtlLocation = Queryable.from(dtlLocations)
                .min(new DtlLocation.DtlNearestComparator(currentLocation));
        onLocationSelected(dtlLocation);
    }

    public void onLocationSelected(DtlLocation location) {
        trackLocationSelection(dtlLocationRepository.getSelectedLocation(), location);
        dtlLocationRepository.persistLocation(location);
        view.showMerchants(new PlacesBundle(location));
    }

    private void setItems() {
        view.setItems(status == Status.NEARBY ? dtlLocations : searchLocations);
    }

    /**
     * Analytic-related
     */
    private void trackLocationSelection(DtlLocation previousLocation, DtlLocation newLocation) {
        if (previousLocation != null)
            TrackingHelper.dtlChangeLocation(newLocation.getId());
        String locationSelectType = status.equals(Status.NEARBY) ?
                TrackingHelper.DTL_ACTION_SELECT_LOCATION_FROM_NEARBY : TrackingHelper.DTL_ACTION_SELECT_LOCATION_FROM_SEARCH;
        TrackingHelper.dtlSelectLocation(locationSelectType, newLocation.getId());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search stuff
    ///////////////////////////////////////////////////////////////////////////

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
            int oldLength = this.caption != null ? this.caption.length() : 0;

            this.caption = caption;

            boolean apiSearch = oldLength < caption.length() &&
                    caption.length() == SEARCH_SYMBOL_COUNT;

            if (caption.length() < SEARCH_SYMBOL_COUNT) flushSearch();
            else if (apiSearch) apiSearch();
            else localSearch();
        }
    }

    private void flushSearch() {
        searchLocations.clear();
        setItems();
    }

    private void apiSearch() {
        view.startLoading();
        view.citiesLoadingStarted();

        if (getDtlLocationsQuery != null) dreamSpiceManager.cancel(getDtlLocationsQuery);

        getDtlLocationsQuery = new GetDtlLocationsQuery(caption);
        doRequest(getDtlLocationsQuery, this::onSearchResultLoaded);
    }

    private void onSearchResultLoaded(ArrayList<DtlLocation> searchLocations) {
        this.searchLocations = searchLocations;
        view.finishLoading();
        localSearch();
    }

    private void localSearch() {
        if (searchLocations != null && !searchLocations.isEmpty())
            view.bind(Observable.from(Queryable
                                    .from(searchLocations)
                                    .filter(dtlLocation ->
                                            dtlLocation.getLongName().toLowerCase().contains(caption))
                                    .sort(new DtlLocation.DtlLocationRangeComparator(caption))
                                    .toList()
                    ).toList().compose(new IoToMainComposer<>())
            ).subscribe(view::setItems, e -> Timber.e(e, "Smth went wrong while search"));
    }

    public interface View extends RxView, ApiErrorView {

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
