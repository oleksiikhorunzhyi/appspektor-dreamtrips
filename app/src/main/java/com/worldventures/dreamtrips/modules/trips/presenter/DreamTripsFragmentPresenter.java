package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateRegionsAndThemesEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DreamTripsFragmentPresenter extends Presenter<DreamTripsFragmentPresenter.View> {

    @Inject
    protected Prefs prefs;

    @Inject
    protected SnappyRepository db;

    private boolean loadFromApi;
    private RoboSpiceAdapterController<TripModel> roboSpiceAdapterController
            = new RoboSpiceAdapterController<TripModel>() {

        @Override
        public SpiceRequest<ArrayList<TripModel>> getRefreshRequest() {
            return new GetTripsQuery(db, prefs, loadFromApi) {
                @Override
                public ArrayList<TripModel> loadDataFromNetwork() throws Exception {
                    return performFiltering(super.loadDataFromNetwork());
                }
            };
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<TripModel> items, SpiceException spiceException) {
            loadFromApi = false;
            view.finishLoading(items);
        }
    };

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private List<Integer> acceptedRegions;
    private List<ActivityModel> acceptedThemes;

    public DreamTripsFragmentPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        dateFilterItem.reset();
        TrackingHelper.dreamTrips(getUserId());
    }

    @Override
    public void resume() {
        if (view.getAdapter().getCount() == 0) {
            roboSpiceAdapterController.setSpiceManager(dreamSpiceManager);
            roboSpiceAdapterController.setAdapter(view.getAdapter());
            roboSpiceAdapterController.reload();
        }
    }

    public void onPause() {
        eventBus.unregister(this);
    }

    public void reload() {
        loadFromApi = true;
        roboSpiceAdapterController.reload();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
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
            roboSpiceAdapterController.reload();
        }
    }

    public void onEvent(TripLikedEvent event) {
        roboSpiceAdapterController.reload();
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
        RequestListener<JsonObject> requestListener = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                trip.setLiked(!trip.isLiked());
                view.dataSetChanged();
                view.showErrorMessage();
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                db.saveTrip(trip);
            }
        };
        if (trip.isLiked()) {
            dreamSpiceManager.execute(new LikeTripCommand(trip.getLikeId()), requestListener);
        } else {
            dreamSpiceManager.execute(new UnlikeTripCommand(trip.getLikeId()), requestListener);
        }
    }

    public void actionMap() {
        fragmentCompass.replace(Route.MAP, null);
    }

    public void onItemClick(TripModel trip) {
        activityRouter.openTripDetails(trip);
    }


    public static interface View extends Presenter.View {
        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading(List<TripModel> items);

        void clearSearch();

        IRoboSpiceAdapter<TripModel> getAdapter();
    }

}