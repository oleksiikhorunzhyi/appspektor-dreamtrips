package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class DreamTripsFragmentPresenter extends BaseDreamTripsPresenter<DreamTripsFragmentPresenter.View> {

    @Inject
    protected Prefs prefs;

    private boolean loadFromApi;
    private boolean loadWithStatus;
    private boolean goneToMap = false;

    private RoboSpiceAdapterController<TripModel> roboSpiceAdapterController = new RoboSpiceAdapterController<TripModel>() {
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
            loadFromApi = false;
            loadWithStatus = false;
            view.finishLoading(items);
            if (spiceException != null) {
                handleError(spiceException);
            } else {
                if (shouldUpdate()) {
                    loadFromApi = true;
                    reload();
                }
            }
        }
    };

    public DreamTripsFragmentPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        TrackingHelper.dreamTrips(getUserId());
    }

    @Override
    public void resume() {
        if (view.getAdapter().getCount() == 0) {
            goneToMap = false;
            roboSpiceAdapterController.setSpiceManager(dreamSpiceManager);
            roboSpiceAdapterController.setAdapter(view.getAdapter());
            roboSpiceAdapterController.reload();
        }
    }

    public void onPause() {
        eventBus.unregister(this);
    }

    public void loadFromApi() {
        loadFromApi = true;
        loadWithStatus = true;
        roboSpiceAdapterController.reload();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            setFilters(event);
        }
        roboSpiceAdapterController.reload();
    }

    public void onEvent(TripLikedEvent event) {
        roboSpiceAdapterController.reload();
    }

    @Override
    public void resetFilters() {
        super.resetFilters();
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

    public void onItemClick(TripModel trip) {
        activityRouter.openTripDetails(trip);
    }


    public interface View extends Presenter.View {
        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading(List<TripModel> items);

        void clearSearch();

        IRoboSpiceAdapter<TripModel> getAdapter();
    }

}