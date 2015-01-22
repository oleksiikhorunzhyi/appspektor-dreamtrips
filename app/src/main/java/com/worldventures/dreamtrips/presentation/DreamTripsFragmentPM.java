package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Edward on 19.01.15.
 * presentation model for fragment with list of the trips
 */
@PresentationModel
public class DreamTripsFragmentPM extends BasePresentation<DreamTripsFragmentPM.View> {

    private static final long DELTA = 10 * 60 * 1000;

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Prefs prefs;

    @Inject
    Context context;

    @Inject
    @Global
    EventBus eventBus;

    List<Trip> data;
    List<Trip> filteredData;
    CollectionController<Trip> tripsController;

    private boolean loadFromApi;
    private boolean fromCash;

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private List<Integer> acceptedRegions;

    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        this.tripsController = loaderFactory.create(0, (context, params) -> {
            if (!fromCash && needUpdate() || loadFromApi) {
                this.loadFromApi = false;
                this.data = this.loadTrips();

                FileUtils.saveJsonToCache(context, this.data, FileUtils.TRIPS);

                prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
            } else {
                this.data = FileUtils.parseJsonFromCache(context, new TypeToken<List<Trip>>() {
                }.getType(), FileUtils.TRIPS);
                fromCash = false;
            }

            filteredData = performFiltering(data);

            return filteredData;
        });
        eventBus.register(this);
    }

    public void onEvent(FilterBusEvent event) {
        if (event.isReset()) {
            resetFilters();
        } else {
            maxPrice = event.getMaxPrice();
            minNights = event.getMinNights();
            minPrice = event.getMinPrice();
            maxNights = event.getMaxNights();
            acceptedRegions = event.getAcceptedRegions();
            fromCash = true;
        }
        tripsController.reload();
    }

    public CollectionController<Trip> getTripsController() {
        return tripsController;
    }

    public void reload() {
        loadFromApi = true;
        tripsController.reload();
    }

    private List<Trip> performFiltering(List<Trip> data) {
        List<Trip> filteredTrips = new ArrayList<>();
        filteredTrips.addAll(Collections2.filter(data, (input) ->
                input.getPrice().getAmount() <= maxPrice
                        && input.getPrice().getAmount() >= minPrice
                        && input.getDuration() >= minNights
                        && input.getDuration() <= maxNights
                        && (acceptedRegions == null || acceptedRegions.contains(input.getRegion().getId()))));
        return filteredTrips;
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.acceptedRegions = null;
    }

    public void onItemLike(Trip trip) {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                FileUtils.saveJsonToCache(context, data, FileUtils.TRIPS);
            }

            @Override
            public void failure(RetrofitError error) {
                trip.setLiked(!trip.isLiked());
                view.dataSetChanged();
                view.showErrorMessage();
            }
        };

        if (trip.isLiked()) {
            dreamTripsApi.likeTrip(trip.getId(), callback);
        } else {
            dreamTripsApi.unlikeTrio(trip.getId(), callback);
        }

    }

    public List<Trip> loadTrips() {
        return dreamTripsApi.getTrips();
    }

    public void onItemClick(int position) {
        activityRouter.openTripDetails(filteredData.get(position));
    }

    public boolean needUpdate() {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA;
    }

    public static interface View extends BasePresentation.View {
        void dataSetChanged();

        void showErrorMessage();
    }

}
