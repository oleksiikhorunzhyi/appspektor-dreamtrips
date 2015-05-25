package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class DreamTripsFragmentPresenter extends Presenter<DreamTripsFragmentPresenter.View> {

    @Inject
    protected Prefs prefs;

    @Inject
    protected SnappyRepository db;

    private boolean loadFromApi;
    private boolean loadWithStatus;
    private boolean goneToMap = false;

    private DreamSpiceAdapterController<TripModel> adapterController
            = new DreamSpiceAdapterController<TripModel>() {

        @Override
        public SpiceRequest<ArrayList<TripModel>> getRefreshRequest() {
            return new GetTripsQuery(db, prefs, loadFromApi || cacheEmpty()) {
                @Override
                public ArrayList<TripModel> loadDataFromNetwork() throws Exception {
                    return performFiltering(super.loadDataFromNetwork());
                }
            };
        }

        private Boolean cacheEmpty() {
            return db.isEmpty(SnappyRepository.TRIP_KEY);
        }

        private boolean shouldUpdate() {
            long current = Calendar.getInstance().getTimeInMillis();
            return current - prefs.getLong(Prefs.LAST_SYNC) > DreamTripsRequest.DELTA_TRIP;
        }

        @Override
        public void onStart(LoadType loadType) {
            if (loadWithStatus || cacheEmpty()) {
                view.startLoading();
            }
        }

        @Override
        public void onFinish(LoadType type, List<TripModel> items, SpiceException spiceException) {
            if (adapterController != null) {
                loadFromApi = false;
                loadWithStatus = false;
                view.finishLoading();
                if (spiceException != null) {
                    handleError(spiceException);
                } else {
                    if (shouldUpdate()) {
                        loadFromApi = true;
                        reload();
                    }
                }
            }
        }
    };

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private List<Integer> acceptedRegions;
    private List<ActivityModel> acceptedThemes;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dateFilterItem.reset();
        TrackingHelper.dreamTrips(getUserId());
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() == 0) {
            goneToMap = false;
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    public void reload() {
        loadFromApi = true;
        loadWithStatus = true;
        adapterController.reload();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            view.startLoading();
            if (event.isReset()) {
                resetFilters();
            } else {
                maxPrice = event.getMaxPrice();
                minNights = event.getMinNights();
                minPrice = event.getMinPrice();
                maxNights = event.getMaxNights();
                dateFilterItem = event.getDateFilterItem();
                acceptedRegions = event.getAcceptedRegions();
                acceptedThemes = event.getAcceptedActivities();
            }
            adapterController.reload();
        }
    }

    public void onEvent(TripLikedEvent event) {
        adapterController.reload();
    }

    private ArrayList<TripModel> performFiltering(List<TripModel> trips) {
        ArrayList<TripModel> filteredTrips = new ArrayList<>();
        filteredTrips.addAll(Queryable.from(trips).filter(input ->
                input.isPriceAccepted(maxPrice, minPrice)
                        && input.isDurationAccepted(maxNights, minNights, dateFilterItem)
                        && input.isCategoriesAccepted(acceptedThemes, acceptedRegions)).toList());

        return filteredTrips;
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.acceptedRegions = null;
        this.acceptedThemes = null;
        dateFilterItem.reset();
        view.clearSearch();
    }

    public void onItemLike(TripModel trip) {
        DreamTripsRequest<JsonObject> request = trip.isLiked() ?
                new LikeTripCommand(trip.getLikeId()) :
                new UnlikeTripCommand(trip.getLikeId());

        doRequest(request, (object) -> onSuccess(trip), (spiceException) -> onFailure(trip));
    }

    private void onSuccess(TripModel trip) {
        db.saveTrip(trip);
    }

    private void onFailure(TripModel trip) {
        trip.setLiked(!trip.isLiked());
        view.dataSetChanged();
        view.showErrorMessage();
    }

    public void actionMap() {
        if (!goneToMap) {
            fragmentCompass.replace(Route.MAP, null);
            goneToMap = true;
        }
    }

    @Override
    public void dropView() {
        adapterController = null;
        super.dropView();
    }

    public void onItemClick(TripModel trip) {
        activityRouter.openTripDetails(trip);
    }


    public interface View extends Presenter.View {
        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading();

        void clearSearch();

        IRoboSpiceAdapter<TripModel> getAdapter();
    }

}