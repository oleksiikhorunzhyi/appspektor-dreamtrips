package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MapFragmentPresenter extends Presenter<MapFragmentPresenter.View> {

    @Inject
    protected SnappyRepository db;

    private List<TripModel> trips = new ArrayList<>();
    private List<TripModel> filteredTrips;
    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut;
    private List<Integer> acceptedRegions;
    private List<ActivityModel> acceptedThemes;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private String query;

    public MapFragmentPresenter(MapFragmentPresenter.View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        dateFilterItem.reset();
    }

    public void onMapLoaded() {
        trips.clear();
        trips.addAll(db.getTrips());
        setFilters(eventBus.getStickyEvent(FilterBusEvent.class));
        performFiltering();
    }

    public void setFilters(FilterBusEvent event) {
        if (event == null || event.isReset()) {
            resetFilters();
        } else {
            maxPrice = event.getMaxPrice();
            minNights = event.getMinNights();
            minPrice = event.getMinPrice();
            maxNights = event.getMaxNights();
            acceptedRegions = event.getAcceptedRegions();
            acceptedThemes = event.getAcceptedActivities();
            showSoldOut = event.isShowSoldOut();
            dateFilterItem = event.getDateFilterItem();
        }
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            setFilters(event);
            performFiltering();
        }
    }

    public void onPause() {
        eventBus.unregister(this);
    }

    private void reloadPins() {
        view.clearMap();
        for (TripModel trip : filteredTrips) {
            view.addPin(new LatLng(trip.getGeoLocation().getLat(),
                    trip.getGeoLocation().getLng()), trip.getId());
        }
    }

    private void performFiltering() {
        if (trips != null) {
            List<TripModel> tempList = new ArrayList<>();
            tempList.addAll(Queryable.from(trips).filter((input) ->
                    input.isPriceAccepted(maxPrice, minPrice)
                            && input.isDurationAccepted(maxNights, minNights, dateFilterItem)
                            && input.isCategoriesAccepted(acceptedThemes, acceptedRegions)).toList());

            filteredTrips.addAll(Queryable.from(tempList).filter((input) -> input.containsQuery(query)).toList());
            reloadPins();
        }
    }

    public void applySearch(String query) {
        this.query = query;
        performFiltering();
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.acceptedRegions = null;
        this.acceptedThemes = null;
        dateFilterItem.reset();
    }

    public void onEvent(InfoWindowSizeEvent event) {
        view.showInfoWindow(event.getOffset());
    }

    public void markerReady() {
        eventBus.post(new ShowInfoWindowEvent());
    }

    public void onMarkerClick(String id) {
        TripModel resultTrip = null;
        int realId = Integer.parseInt(id);
        for (TripModel trip : filteredTrips) {
            if (trip.getId() == realId) {
                resultTrip = trip;
                break;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentMapTripInfo.EXTRA_TRIP, resultTrip);
        fragmentCompass.add(Route.MAP_INFO, bundle);
    }

    public void onCameraChanged() {
        if (fragmentCompass.getCurrentFragment() instanceof FragmentMapTripInfo) {
            fragmentCompass.pop();
        }
    }

    public void actionList() {
        fragmentCompass.pop();
    }

    public interface View extends Presenter.View {
        public void addPin(LatLng latLng, int id);

        public void clearMap();

        public void showInfoWindow(int offset);
    }
}
